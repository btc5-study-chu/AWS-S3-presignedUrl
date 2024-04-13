import {render, screen, waitFor, within} from "@testing-library/react";
import {ApplicationContext, ApplicationState, UploadList} from "../../contexts/ApplicationProvider.tsx";
import {FileUpload} from "../../pages/FileUpload.tsx";
import {DefaultApplicationProvider} from "../utils/DefaultApplicationProvider.ts";
import {ReactNode} from "react";
import {userEvent} from "@testing-library/user-event";
import {vi} from "vitest";
import {fileUploadService} from "../../service/FileUploadService.ts";
import {responsePresignedUrl} from "../../repository/FileUploadRepository.ts";

vi.mock("../../service/FileUploadService.ts")

const originalAlert = window.alert
describe("FileUploadのテスト",()=>{

    beforeEach(()=>{
        vi.mocked(fileUploadService.getAllImages).mockImplementation(()=> Promise.resolve([]))
    })

    afterEach(()=>{
        vi.stubGlobal("alert",originalAlert)
    })

    describe("FileUploadがレンダーされると",()=>{
        test("正しい要素が表示されている",()=>{
            renderWithContext({
                children: <FileUpload />,
                contexts: {
                    uploadList : [
                       dummyData[0],
                        dummyData[1]
                    ]
                }
            })

            const topElement = screen.getByTestId("FileUploadPage")
            expect(topElement.firstChild).toHaveAttribute("type","file")
            expect(topElement.firstChild).toHaveProperty("multiple",true)
            expect(within(topElement).getByRole("button",{name: "アップロード"})).toBeInTheDocument()

            const uploadListElement = screen.getByTestId("uploadListArea")
            expect(within(uploadListElement.children[0] as HTMLElement).getByRole("checkbox")).toBeInTheDocument()
            expect(within(uploadListElement.children[0] as HTMLElement).getByText("test1")).toBeInTheDocument()
            expect(within(uploadListElement.children[0] as HTMLElement).getByText("fileName1")).toBeInTheDocument()
            expect(within(uploadListElement.children[1] as HTMLElement).getByRole("checkbox")).toBeInTheDocument()
            expect(within(uploadListElement.children[1] as HTMLElement).getByText("test2")).toBeInTheDocument()
            expect(within(uploadListElement.children[1] as HTMLElement).getByText("fileName2")).toBeInTheDocument()
            expect(within(uploadListElement).getByRole("button",{name:"画像を取得する"})).toBeInTheDocument()
        })

        test("service層のgetAllImagesを呼び、その返り値をsetUploadListの引数にして呼ぶ",()=>{
            const spyGetAllImages = vi.fn(()=> Promise.resolve([dummyData[0],dummyData[1]]))
            vi.mocked(fileUploadService.getAllImages).mockImplementation(spyGetAllImages)

            const spySetUploadList = vi.fn()


                renderWithContext({
                    children: <FileUpload/>,
                    contexts: {
                        setUploadList: spySetUploadList
                    }
                })


            expect(spyGetAllImages).toHaveBeenCalled()
            waitFor(()=>{
                expect(spySetUploadList).toHaveBeenCalledWith(dummyData)
            })
        })


    })


    test("handleFileChangeについて、ファイルが選択されたらsetFilesを正しい引数で呼ぶ",async ()=>{
        const spySetFiles = vi.fn()
        const files = [
            new File(['hello'], 'hello.png', {type: 'image/png'}),
            new File(['world'], 'world.txt', {type: 'text/plain'})
        ];


        renderWithContext({
            children: <FileUpload />,
            contexts: {
                setFiles:spySetFiles
            }
        })

        const input = screen.getByTestId("FileUploadPage").firstChild as HTMLInputElement
        await userEvent.upload(input, files);

        expect(spySetFiles).toHaveBeenCalledWith(files)
    })

    test("アップロードボタンを押したら、filesがから配列の場合、window.alertが正しく呼ばれる",async ()=>{
        const spyAlert = vi.fn()
        vi.stubGlobal("alert",spyAlert)

        renderWithContext({
            children: <FileUpload />,
            contexts: {
                files:[]
            }
        })

        await userEvent.click(screen.getByRole("button",{name : "アップロード"}))

        expect(spyAlert).toHaveBeenCalledWith("ファイルが選択されていません。")
    })

    test("アップロードボタンを押したら、fileUploadServiceのgetPresignedUrlを正しい引数で呼んで、その返り値を使って正しい引数でuploadToS3を呼んでalertを正しく表示する",async ()=>{
        const dummyFiles = [
            new File(['hello'], 'hello.png', {type: 'image/png'}),
            new File(['world'], 'world.txt', {type: 'text/plain'})
        ];
        const dummyResponse:responsePresignedUrl[] = [
            {fileName: "testFile1", url:"testUrl1"},
            {fileName: "testFile2", url:"testUrl2"},
        ]
        const spyPutPresigneUrl = vi.fn(()=> Promise.resolve(dummyResponse))
        vi.mocked(fileUploadService.putPresignedUrl).mockImplementation(spyPutPresigneUrl)

        const spyUploadToS3First = vi.fn()
        const spyUploadToS3Second = vi.fn()
        vi.mocked(fileUploadService.uploadToS3).mockImplementationOnce(spyUploadToS3First)
        vi.mocked(fileUploadService.uploadToS3).mockImplementationOnce(spyUploadToS3Second)

        const spyAlert = vi.fn()
        vi.stubGlobal("alert",spyAlert)


        renderWithContext({
            children: <FileUpload />,
            contexts: {
                files:dummyFiles
            }
        })
        await userEvent.click(screen.getByRole("button",{name : "アップロード"}))

        expect(spyPutPresigneUrl).toHaveBeenCalledWith(dummyFiles)
        expect(spyUploadToS3First).toHaveBeenCalledWith(dummyResponse[0].url,dummyFiles[0])
        expect(spyUploadToS3Second).toHaveBeenCalledWith(dummyResponse[1].url,dummyFiles[1])
        expect(spyAlert).toHaveBeenCalledWith('ファイルのアップロードに成功しました。')
    })
})

const dummyData:UploadList[] = [
    {id:"test1",fileName:"fileName1"},
    {id:"test2",fileName:"fileName2"},
]


type renderArg = {
    children:ReactNode,
    contexts?:Partial<ApplicationState>
}

const renderWithContext = ({children, contexts}:renderArg) => {
    render(
        <ApplicationContext.Provider value={
            {
                ...DefaultApplicationProvider,
                ...contexts
            }
        }>
            {children}
        </ApplicationContext.Provider>
    )
}
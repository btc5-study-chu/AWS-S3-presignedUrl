import {ReactNode, useContext} from "react";
import {ApplicationContext, ApplicationProvider, UploadList} from "../../contexts/ApplicationProvider.tsx";
import {act, renderHook} from "@testing-library/react";

describe("ApplicationProviderのtest",()=>{
    test("setFilesに私た引数に応じて正しくfilesが変更される",()=>{
        const testDate:File[] = [
            {type: "hogehoge", name :"hugahuga"} as File,
            {type: "hogehoge1", name:"hugahuga1"} as File
        ]
        const result = renderContext()

        act(()=>{
            result.current?.setFiles(testDate)
        })

        expect(result.current?.files).toEqual(testDate)
    })
    test("setUploadListに渡した引数に応じて正しくuploadListが変更される",()=>{
        const testDate:UploadList[] = [
            {id: "1234",fileName:"test1"},
            {id: "5678",fileName:"test2"},
        ]
        const result = renderContext()

        act(()=>{
            result.current?.setUploadList(testDate)
        })

        expect(result.current?.uploadList).toEqual(testDate)
    })
})


const useGetContext = () => useContext(ApplicationContext)

const renderContext = () => {
    const wrapper = ({ children }: { children: ReactNode }) => (
        <ApplicationProvider>{children}</ApplicationProvider>
    )

    return  renderHook(useGetContext, { wrapper }).result
}
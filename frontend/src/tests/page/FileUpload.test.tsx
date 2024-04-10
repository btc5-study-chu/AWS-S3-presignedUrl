import {render, screen, within} from "@testing-library/react";
import {ApplicationContext, ApplicationState} from "../../contexts/ApplicationProvider.tsx";
import {FileUpload} from "../../pages/FileUpload.tsx";
import {DefaultApplicationProvider} from "../utils/DefaultApplicationProvider.ts";
import {ReactNode} from "react";

describe("FileUploadのテスト",()=>{
    test("FileUploadがレンダーされると正しい要素が表示されている",()=>{
        renderWithContext({
            children: <FileUpload />
        })

        const topElement = screen.getByTestId("FileUploadPage")

        expect(topElement.firstChild).toHaveAttribute("type","file")
        expect(topElement.firstChild).toHaveProperty("multiple",true)
        expect(within(topElement).getByRole("button",{name: "アップロード"})).toBeInTheDocument()
    })
})

type renderArg = {
    children:ReactNode,
    contexts?:ApplicationState
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
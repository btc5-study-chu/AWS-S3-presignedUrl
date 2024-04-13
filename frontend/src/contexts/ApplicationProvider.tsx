import {createContext, useState} from "react";
import * as React from "react";

type Props = {
    children: React.ReactNode
}

export type UploadList = {
    id : string,
    fileName : string
}

export type ApplicationState = {
    files:File[]
    setFiles:(files:File[])=>void
    uploadList: UploadList[],
    setUploadList:(uploadList:UploadList[])=>void
}


export const ApplicationContext = createContext<ApplicationState | null>(null)
export const ApplicationProvider: React.FC<Props> = ({children}) => {

    const [files, setFiles] = useState<File[]>([]);
    const [uploadList, setUploadList] = useState<UploadList[]>([])

    return <>
        <ApplicationContext.Provider value={
            {
                files,
                setFiles,
                uploadList,
                setUploadList
            }
        }>
            {children}
        </ApplicationContext.Provider>
    </>
}
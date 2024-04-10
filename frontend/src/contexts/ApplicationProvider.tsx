import {createContext, useState} from "react";
import * as React from "react";

type Props = {
    children: React.ReactNode
}

export type ApplicationState = {
    files:File[]
    setFiles:(files:File[])=>void
}


export const ApplicationContext = createContext<ApplicationState | null>(null)
export const ApplicationProvider: React.FC<Props> = ({children}) => {

    const [files, setFiles] = useState<File[]>([]);

    return <>
        <ApplicationContext.Provider value={
            {
                files,
                setFiles
            }
        }>
            {children}
        </ApplicationContext.Provider>
    </>
}
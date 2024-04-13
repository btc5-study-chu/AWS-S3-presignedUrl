import {fileUploadRepository} from "../repository/FileUploadRepository.ts";

export type requestBodyType = {
    files: { contentType: string, fileName: string }[]
}

class FileUploadService {

    async putPresignedUrl(files: File[]) {
        const requestBody = this.createReqestBody(files)
        try {
            const result = await fileUploadRepository.getPresignedUrl(requestBody)
            return result
        } catch (err) {
            throw new Error(`err : ${err}`)
        }
    }

    async uploadToS3(url:string,file:File){
        await fileUploadRepository.uploadToS3(url,file)
    }

    private createReqestBody = (files: File[]): requestBodyType => {
        const typeAndName = files.map(elm => {
            return ({
                contentType: elm.type,
                fileName: elm.name
            })
        })
        return ({
            files: typeAndName
        })
    }
}

export const fileUploadService = new  FileUploadService()


import {fileUploadRepository, requestGetPresignedUrl} from "../repository/FileUploadRepository.ts";
import {UploadList} from "../contexts/ApplicationProvider.tsx";

export type requestBodyType = {
    files: { contentType: string, fileName: string }[]
}

class FileUploadService {

    async putPresignedUrl(files: File[]) {
        const requestBody = this.createReqestBody(files)
        try {
            const result = await fileUploadRepository.putPresignedUrl(requestBody)
            return result
        } catch (err) {
            throw new Error(`err : ${err}`)
        }
    }

    async uploadToS3(url:string,file:File){
        await fileUploadRepository.uploadToS3(url,file)
    }

    async getAllImages():Promise<UploadList[]>{
        return fileUploadRepository.getAllImages()
    }

    async getPresignedUrl(checkList:string[]) {
        const reqBody: requestGetPresignedUrl[] = checkList.map(elm => ({id:elm}) )
        return await fileUploadRepository.getPresignedUrl(reqBody)
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


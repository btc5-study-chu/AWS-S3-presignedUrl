import {requestBodyType} from "../service/FileUploadService.ts";
import axios from "axios";

export type responsePresignedUrl = {
    fileName:string,
    url:string
}

class FileUploadRepository {
    async getPresignedUrl(reqBody:requestBodyType){
        try {
            const result:responsePresignedUrl[] = await axios.post('/api/images/presignedUrls',reqBody).then(elm => elm.data)
            return result
        } catch (err) {
            throw new Error(`err : ${err}`)
        }
    }

    async uploadToS3(url:string,file:File){
        await axios.put(url,file,{
            headers: {
                'Content-Type': file.type
            }
        })
    }
}

export const fileUploadRepository = new FileUploadRepository()
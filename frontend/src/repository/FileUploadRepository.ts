import {requestBodyType} from "../service/FileUploadService.ts";
import axios from "axios";
import {UploadList} from "../contexts/ApplicationProvider.tsx";

export type responsePresignedUrl = {
    fileName:string,
    url:string
}

class FileUploadRepository {
    async getPresignedUrl(reqBody:requestBodyType){
        try {
            const result:responsePresignedUrl[] = await axios.post('/api/images/putPresignedUrls',reqBody).then(elm => elm.data)
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

    async getAllImages():Promise<UploadList[]>{
        return await axios.get("api/images").then(elm => elm.data)
    }
}

export const fileUploadRepository = new FileUploadRepository()
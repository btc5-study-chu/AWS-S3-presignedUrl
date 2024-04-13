import React, {useContext} from 'react';
import {ApplicationContext} from "../contexts/ApplicationProvider.tsx";
import {fileUploadService} from "../service/FileUploadService.ts";

export const FileUpload: React.FC = () => {
    // const [files, setFiles] = useState<File[]>([]);

    const {files, setFiles} = useContext(ApplicationContext)!
    const handleFileChange = (event: React.ChangeEvent<HTMLInputElement>) => {
        const filesData = event.target.files;
        if (filesData && filesData.length > 0) {
            setFiles(Array.from(filesData));
        }
    };

    const uploadFileToS3 = async () => {
        if (files.length === 0) {
            alert('ファイルが選択されていません。');
            return;
        }

        try {
            const results = await fileUploadService.putPresignedUrl(files)

            for (const result of results) {
                const index = results.indexOf(result);
                await fileUploadService.uploadToS3(result.url,files[index])
            }
            alert('ファイルのアップロードに成功しました。');
        } catch (error) {
            console.error('ファイルのアップロードに失敗しました。', error);
            alert('ファイルのアップロードに失敗しました。');
        }
    };



    return (
        <div data-testid="FileUploadPage">
            <input type="file" multiple={true} onChange={handleFileChange} />
            <button onClick={uploadFileToS3}>アップロード</button>
        </div>
    );
};



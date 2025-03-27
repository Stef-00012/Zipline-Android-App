import { useLocalSearchParams, useRouter } from "expo-router";
import { getFolder, getFolders } from "@/functions/zipline/folders";

export default function FolderUpload() {
    const searchParams = useLocalSearchParams<{
        folderId?: string;
    }>();
    
    const [folder, setFolder] = useState<APIFolder>(null)
    
    useEffect(() => {
        (async () => {
            const folder = await getFolder(searchParams.folderId)
            
            setFolder(typeof folder === "string" ? null : folder)
        })
    })
    
    return (
        
    )
}
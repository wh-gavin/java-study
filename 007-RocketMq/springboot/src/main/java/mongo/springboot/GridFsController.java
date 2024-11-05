package mongo.springboot;

import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.mongodb.client.gridfs.model.GridFSFile;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
 
@RestController
@RequestMapping("/fs")
public class GridFsController {
 
    public static final Logger log = LoggerFactory.getLogger(GridFsController.class);
 
    @Resource
    private GridFsService gridFsService;
 
    /**
     * 上传文件
     */
    @SneakyThrows
    @PostMapping("/uploadFile")
    public ResponseBean<?> uploadSingleFile(MultipartFile file) {
        log.info("接受文件: {}", file.getOriginalFilename());
        ObjectId objectId = gridFsService.uploadFile(file);
        return new ResponseBean<>(objectId.toString(), "上传文件到默认bucket成功, objectId: " + objectId, 200);
    }
 
    /**
     * 根据文件id获得文件的基本信息
     */
    @GetMapping(value = "/getFileById")
    public ResponseBean<?> getFileInfoById(@RequestParam(name = "fileId") String fileId) {
        GridFSFile gridFSFile = gridFsService.getFileById(fileId);
        if (gridFSFile == null) {
            return new ResponseBean<>("文件不存在", "fail", 400);
        }
        return new ResponseBean<>(gridFSFile, "success", 200);
    }
 
    /**
     * 根据文件id删除文件
     */
    @GetMapping(value = "/deleteFileById")
    public ResponseBean<?> deleteFileById(@RequestParam(name = "fileId") String fileId) {
        gridFsService.deleteFileById(fileId);
        return new ResponseBean<>("", "删除成功", 200);
    }
 
    /**
     * 根据文件id下载文件，写法一
     */
    @GetMapping(value = "/downloadFileById")
    @SneakyThrows
    public byte[] downloadFileById(@RequestParam("fileId") String fileId,
                                   HttpServletResponse response) {
        return gridFsService.downloadFileById(fileId, response);
    }
 
    /**
     * 根据文件id下载文件,写法二  "c://e_file//abb.jpg"
     */
    @GetMapping(value = "/downloadFileById2")
    @SneakyThrows
    public void downloadFileById2(@RequestParam("fileId") String fileId,
                                  @RequestParam("filePath") String filePath) {
        gridFsService.downloadFileById2(fileId, filePath);
    }
}
package mongo.springboot;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.model.GridFSFile;

import cn.hutool.core.io.IoUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
 
@Service
public class GridFsService {
    private static final Logger log = LoggerFactory.getLogger(GridFsService.class);
 
    @Resource
    private GridFSBucket gridFSBucket;
    @Resource
    private GridFsTemplate gridFsTemplate;
 
    @SneakyThrows
    public ObjectId uploadFile(MultipartFile file) {
        String contentType = file.getContentType();
        String filename = file.getOriginalFilename();
        InputStream inputStream = file.getInputStream();
        // 上传文件中我们也可以使用DBObject附加一些属性
        DBObject metadata = new BasicDBObject();
        metadata.put("name", "mms");
        return gridFsTemplate.store(inputStream, filename, contentType, metadata);
    }
 
    public GridFSFile getFileById(String fileId) {
        Query query = Query.query(Criteria.where("_id").is(fileId));
        GridFSFile gridFsFile = gridFsTemplate.findOne(query);
        String key = (String) gridFsFile.getMetadata().get("name");
        log.info("gridFsFile metadata key: {}", key);
        return gridFsFile;
    }
 
    public void deleteFileById(String fileId) {
        Query query = Query.query(Criteria.where("_id").is(fileId));
        GridFSFile file = gridFsTemplate.findOne(query);
        if (null == file) {
            log.info("文件删除失败，文件不存在");
            return;
        }
        gridFsTemplate.delete(query);
    }
 
    /**
     * 从mongo下载文件到浏览器保存
     */
    @SneakyThrows
    public byte[] downloadFileById(String fileId, HttpServletResponse response) {
        Query query = Query.query(Criteria.where("_id").is(fileId));
        GridFSFile gridFsFile = gridFsTemplate.findOne(query);
 
        // 解决文件下载的时候，中文乱码的问题
        String name = new String(gridFsFile.getFilename().getBytes("GBK"), "ISO-8859-1");
        response.setHeader("Content-Disposition", "attachment;fileName=" + name);
        GridFSDownloadStream downloadStream = gridFSBucket.openDownloadStream(gridFsFile.getObjectId());
 
        //创建gridFsResource，用于获取流对象
        GridFsResource gridResource = new GridFsResource(gridFsFile, downloadStream);
        log.info(gridFsFile.getFilename() + ">>文件下载成功!!");
        return IoUtil.readBytes(gridResource.getInputStream());
    }
 
    /**
     * 从mongo中下载文件到指定目录
     */
    @SneakyThrows
    public void downloadFileById2(String fileId, String filePath) {
        Query query = Query.query(Criteria.where("_id").is(fileId));
        GridFSFile gridFsFile = gridFsTemplate.findOne(query);
        GridFsResource resource = gridFsTemplate.getResource(gridFsFile);
 
        File file = new File(filePath);
        FileOutputStream outputStream = new FileOutputStream(file);
        IoUtil.copy(resource.getInputStream(), outputStream);
        outputStream.close();
        log.info(gridFsFile.getFilename() + ">>文件下载成功!!");
    }
}
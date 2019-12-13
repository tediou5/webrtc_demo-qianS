package cn.teclub.ha3.coco_server.network.impl;


import cn.teclub.ha3.coco_server.network.StIUploadFile;
import cn.teclub.ha3.coco_server.sys.StApplicationProperties;
import cn.teclub.ha3.utils.StObject;
import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

import java.io.File;
import java.util.ResourceBundle;

/**
 * upload images to AWS
 * @author Tao Zhang
 */
public class StAwsUploadFileImpl extends StObject implements StIUploadFile {

    private final String profile;
    private String bucketName;
    private String clientRegion;
    private String configPath;

    private AmazonS3 s3Client = null;

    public StAwsUploadFileImpl(StApplicationProperties properties){
        profile = properties.getActive();
        bucketName = properties.getAwsBucketName();
        clientRegion = properties.getAwsClientRegion();
        configPath = properties.getS3configPath();
    }

    @Override
    public String uploadImage(String fileName, File file) {
        if (s3Client == null) {
            s3Client = createS3Client();
        }

        try {
            PutObjectRequest request = new PutObjectRequest(bucketName, fileName, file);
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType("image/*");
            request.setMetadata(metadata);
            request.withCannedAcl(CannedAccessControlList.PublicRead);
            s3Client.putObject(request);
            //get request
            GeneratePresignedUrlRequest urlRequest = new GeneratePresignedUrlRequest(bucketName, fileName);
            //generate a public url
            s3Client.generatePresignedUrl(urlRequest);
            return s3Client.getUrl(bucketName,fileName).toString();
        } catch (AmazonServiceException ase) {
            log.error("AmazonServiceException.", ase);
        } catch (AmazonClientException ace) {
            log.error("AmazonServiceException.", ace);
        }

        return null;
    }
    private AmazonS3 createS3Client() {

        log.info("aws-upload-Region:"+clientRegion);
        log.info("aws-upload-Credentials(path,name):"+configPath+','+profile);
        s3Client = AmazonS3ClientBuilder.standard()
                .withCredentials(new ProfileCredentialsProvider(configPath, profile))
                .withRegion(clientRegion)
                .build();
        return s3Client;
    }
}

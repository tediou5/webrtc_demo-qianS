package cn.teclub.ha3.server.common.impl;



import cn.teclub.ha3.server.common.StIUploadFile;
import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class StAwsUploadFileImpl implements StIUploadFile {
    private static final Logger LOGGER = LoggerFactory.getLogger(StAwsUploadFileImpl.class);
    private final String profile = ResourceBundle.getBundle("application")
            .getString("spring.profiles.active");
    private String bucketName = ResourceBundle.getBundle("application")
            .getString("aws-bucket-name");
    private String clientRegion = ResourceBundle.getBundle("application")
            .getString("aws-clientRegion");

    private AmazonS3 s3Client = null;

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

            //获取一个request
            GeneratePresignedUrlRequest urlRequest = new GeneratePresignedUrlRequest(bucketName, fileName);
            //生成公用的url
            URL url = s3Client.generatePresignedUrl(urlRequest);
            return s3Client.getUrl(bucketName,fileName).toString();
        } catch (AmazonServiceException ase) {
            LOGGER.error("AmazonServiceException.", ase);
        } catch (AmazonClientException ace) {
            LOGGER.error("AmazonServiceException.", ace);
        }

        return null;
    }
    private AmazonS3 createS3Client() {
        String configPath = ResourceBundle.getBundle("application")
                .getString("feisuo.s3configPath");
        LOGGER.info("aws-upload-Region:"+clientRegion);
        LOGGER.info("aws-upload-Credentials(path,name):"+configPath+','+profile);
        s3Client = AmazonS3ClientBuilder.standard()
                .withCredentials(new ProfileCredentialsProvider(configPath, profile))
                .withRegion(clientRegion)
                .build();
        return s3Client;
    }
}

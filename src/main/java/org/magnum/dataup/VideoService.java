package org.magnum.dataup;

import org.magnum.dataup.model.Video;
import org.magnum.dataup.model.VideoStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class VideoService {

    private static final AtomicLong currentId = new AtomicLong(0L);
    private List<Video> videoList = new ArrayList<>();
    private Map<Long, Video> videos = new HashMap<Long, Video>();


    public Collection<Video> getVideoList() {
        return videoList;
    }

    public Video addVideo(Video video) {
        checkAndSetId(video);
        String dataUrl = getDataUrl(video.getId());
        video.setDataUrl(dataUrl);
        videoList.add(video);
        save(video);
        return video;
    }

    public VideoStatus uploadVideo(Long id, MultipartFile file, HttpServletResponse response) {

        Video video = videos.get(id);
        VideoStatus videoStatus = new VideoStatus(VideoStatus.VideoState.PROCESSING);
        if (video != null) {
            try {
                VideoFileManager videoFileManager = VideoFileManager.get();
                videoFileManager.saveVideoData(video, file.getInputStream());
                return new VideoStatus(VideoStatus.VideoState.READY);
            } catch (IOException e) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                e.printStackTrace();
            }

        }
        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        return videoStatus;


    }

    public HttpServletResponse getVideoData(Long id, HttpServletResponse response) {
        try {
            VideoFileManager videoFileManager = VideoFileManager.get();
            Video video = videos.get(id);
            if (video == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            } else {
                if (response.getContentType() == null) {
                    response.setContentType("video/mp4");
                }
                videoFileManager.copyVideoData(video, response.getOutputStream());
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
        return response;

    }

    public Video save(Video entity) {
        checkAndSetId(entity);
        videos.put(entity.getId(), entity);
        return entity;
    }

    private void checkAndSetId(Video entity) {
        if (entity.getId() == 0) {
            entity.setId(currentId.incrementAndGet());
        }
    }

    private String getDataUrl(long videoId) {
        String url = getUrlBaseForLocalServer() + "/video/" + videoId + "/data";
        return url;
    }

    private String getUrlBaseForLocalServer() {
        HttpServletRequest request =
                ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String base =
                "http://" + request.getServerName()
                        + ((request.getServerPort() != 80) ? ":" + request.getServerPort() : "");
        return base;
    }


}

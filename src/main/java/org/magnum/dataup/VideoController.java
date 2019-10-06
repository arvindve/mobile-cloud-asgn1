package org.magnum.dataup;

import org.magnum.dataup.model.Video;
import org.magnum.dataup.model.VideoStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import retrofit.http.Streaming;

import javax.servlet.http.HttpServletResponse;
import java.util.Collection;

@Controller
public class VideoController {

    @Autowired
    private VideoService videoService;

    @ResponseBody
    @RequestMapping(method = RequestMethod.GET, value = VideoSvcApi.VIDEO_SVC_PATH)
    public Collection<Video> getVideoList() {
        return videoService.getVideoList();
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.POST, value = VideoSvcApi.VIDEO_SVC_PATH)
    public Video addVideo(@RequestBody Video video) {
        return videoService.addVideo(video);
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.POST, value = VideoSvcApi.VIDEO_DATA_PATH,
            consumes = org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public VideoStatus setVideoData(@PathVariable("id") Long id,
                                    @RequestPart("data") MultipartFile file,
                                    HttpServletResponse response) {
        return videoService.uploadVideo(id, file, response);
    }

    @RequestMapping(value = VideoSvcApi.VIDEO_DATA_PATH, method = RequestMethod.GET)
    @Streaming
    public HttpServletResponse getData(@PathVariable("id") Long id, HttpServletResponse response) {
        return videoService.getVideoData(id, response);
    }


}

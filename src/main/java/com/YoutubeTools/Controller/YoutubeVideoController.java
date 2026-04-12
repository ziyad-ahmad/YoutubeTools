package com.YoutubeTools.Controller;

import com.YoutubeTools.Model.VideoDetails;
import com.YoutubeTools.Service.ThumbnailService;
import com.YoutubeTools.Service.YoutubeService;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class YoutubeVideoController {
  private final  YoutubeService youtubeService;
    private final ThumbnailService thumbnailService;

    @GetMapping("/youtube/video-details")
    public String showvideoform(){
        return "video-details";
    }

    @PostMapping("/youtube/video-details")
    public String fetchvideodetails(@RequestParam String videoUrlOrId, Model model){
    String videoId=thumbnailService.extractVideoID(videoUrlOrId);
    if(videoId==null){
        model.addAttribute("error","Invalid url id");
        return "video-details";
    }
    VideoDetails details=youtubeService.getVideoDetails(videoId);
    if(details==null){
        model.addAttribute("error","video not found");
    }
    else {
        model.addAttribute("videoDetails",details);
    }
    model.addAttribute("videoUrlOrId",videoId);
    return "video-details";
    }

}

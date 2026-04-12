package com.YoutubeTools.Controller;

import com.YoutubeTools.Service.ThumbnailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller

public class ThumbnailController {
    @Autowired
    ThumbnailService thumbnailService;

    @GetMapping("/thumbnail")
    public String home() {
        return "thumbnails";
    }

    @PostMapping("/get-thumbnail")
    public String getThumbnail(@RequestParam("videoUrlOrId") String videoUrlOrId, Model model) {

        String videoId=thumbnailService.extractVideoID(videoUrlOrId);
        if(videoId ==null){
            model.addAttribute("error","invalid youtube url");
            return "thumbnails";
        }
        String Thumbnailurl="https://img.youtube.com/vi/" + videoId + "/maxresdefault.jpg";

        model.addAttribute("thumbnailUrl",Thumbnailurl);
        model.addAttribute("videoId", videoId);
        return "thumbnails";
    }
}

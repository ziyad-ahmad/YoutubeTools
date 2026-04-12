package com.YoutubeTools.Service;

import com.YoutubeTools.Model.SearchVideo;
import com.YoutubeTools.Model.Video;
import com.YoutubeTools.Model.VideoDetails;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor

public class YoutubeService {
    private final WebClient.Builder webClientBuilder;

    @Value("${youtube.api.key}")
    private String apikey;

    @Value("${youtube.api.base.url}")
    private String baseUrl;

    @Value("${youtube.api.max.related.videos}")
    private int maxvidoes;

    public SearchVideo searchVideos(String videoTitle){
        List<String> videoIds=searchforVideoIds(videoTitle);
        if(videoIds.isEmpty()){
            return SearchVideo.builder().primaryvideo(null)
                    .relatedvideos(Collections.emptyList())
                    .build();
        }


        String primaryvideoId=videoIds.get(0);
        List<String> relatedvideoid=videoIds.subList(1,Math.min(videoIds.size(),maxvidoes + 1));

        Video primaryvideos = getvideoById(primaryvideoId);
        List<Video> relatedvideo=new ArrayList<>();

        for (String id:relatedvideoid){
            Video video=getvideoById(id);
            if(video!=null){
                relatedvideo.add(video);

            }
        }

            return SearchVideo.builder().primaryvideo(primaryvideos)
                    .relatedvideos(relatedvideo)
                    .build();
    }
    public List<String> searchforVideoIds(String videoTitle){
                                                                     //jo b response aayega wo is object mai store hoga

    SearchApiresponse searchApiresponse = webClientBuilder.baseUrl(baseUrl).build()
            .get().uri(uriBuilder -> uriBuilder     // query ko safely import karta hai
                    .path("/search")                            //endpoint basically ek url create krahe hai search
                    .queryParam("part","snippet")       // snippet has diffetrn information like title description,tags etc...
                    .queryParam("q",videoTitle)
                    .queryParam("type","video")
                    .queryParam("maxResults",maxvidoes + 1)          //
                    .queryParam("key",apikey)
                    .build())
            .retrieve()                                     // response lene ko ready hota hai jaise hi response ayega wo le lega
            .bodyToMono(SearchApiresponse.class)  // response body ko dto class k object mai convert kr dega
            .block();                               // actual object milega

        if(searchApiresponse == null || searchApiresponse.items==null){
            return Collections.emptyList();
        }
        List<String> videoids=new ArrayList<>();

        for(SearchItem item:searchApiresponse.items){
            videoids.add(item.id.videoId);
        }
        return videoids;

     }

    public VideoDetails getVideoDetails(String videoId){
        videoApiresponse respone=webClientBuilder.baseUrl(baseUrl).build()
                .get()
                .uri(uriBuilder ->uriBuilder
                        .path("/videos")
                        .queryParam("part","snippet")
                        .queryParam("id",videoId)
                        .queryParam("key",apikey)
                        .build())
                .retrieve().bodyToMono(videoApiresponse.class)
                .block();
        if(respone == null || respone.items==null){
            return null;

        }
        Snippet snippet=respone.items.get(0).snippet;
        String thumbnailUrl = "";
        if (snippet.thumbnails != null) {
            thumbnailUrl = snippet.thumbnails.getBestThumbnailUrl();
        }
        return VideoDetails.builder()
                .id(videoId)
                .title(snippet.title)
                .description(snippet.description)
                .tags(snippet.tags==null ?
                        Collections.emptyList():snippet.tags)
                .thumbnailUrl(thumbnailUrl)
                .channelTitle(snippet.channelTitle)
                .publishedAt(snippet.publishedAt)

                .build();
    }




    private Video getvideoById(String videoId){
        videoApiresponse respone=webClientBuilder.baseUrl(baseUrl).build()
                .get()
                .uri(uriBuilder ->uriBuilder
                        .path("/videos")
                        .queryParam("part","snippet")
                        .queryParam("id",videoId)
                        .queryParam("key",apikey)
                        .build())
                .retrieve().bodyToMono(videoApiresponse.class)
                .block();
        if(respone == null || respone.items==null){
            return null;

        }
        Snippet snippet=respone.items.get(0).snippet;
        return Video.builder().Id(videoId).channelTitle(snippet.channelTitle)
                .title(snippet.title)
                .tags(snippet.tags==null ?
                         Collections.emptyList(): snippet.tags)
                .build();
    }


    @Data
    static class SearchApiresponse{
        List<SearchItem> items;

    }
    @Data
    static class SearchItem{
        Id id;
        }
        @Data
    static class Id{
        String videoId;
    }
    @Data
    static class videoApiresponse{
        List<videoItem> items;
    }
    @Data
    static class videoItem{
        Snippet snippet;
    }
    @Data
    static class Snippet{
        String title;
        String description;
        String channelTitle;
        String publishedAt;
        List<String> tags;

        Thumbnails thumbnails;
    }

    @Data
    static class Thumbnails {
        Thumbnail maxres;
        Thumbnail high;
        Thumbnail medium;
        Thumbnail _default;


        String getBestThumbnailUrl() {
            if (maxres != null)
                return maxres.url;
            if (high != null)
                return high.url;
            if (medium != null)
                return medium.url;
           return  _default != null ?  _default.url :" ";

        }
    }

    @Data
    static class  Thumbnail{
        String url;
    }
}

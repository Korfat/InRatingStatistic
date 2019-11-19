package inrating.statistic.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import inrating.statistic.entity.Comment;
import inrating.statistic.entity.Like;
import inrating.statistic.entity.Mention;
import inrating.statistic.entity.Post;
import inrating.statistic.entity.Repost;
import inrating.statistic.service.StatisticService;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

@Service
public class StatisticServiceImpl implements StatisticService {
    private static final String CONTENT_TYPE = "content-type";
    private static final String APPLICATION_JSON = "application/json";
    private static final String ENCODING_UTF = "UTF-8";
    private static final String AUTHORIZATION = "Authorization";
    private static final String TOKEN = "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiIsImp0aSI6ImRi"
            + "YTYzMGE0YzIxYWZlNzRhYTVlNTgwZjBiZjliMDQ1YThmYzY4NWViMjE0ZmFmZTY4ZDAzMGQzZjdiNThhM"
            + "Dg3M2M1MzU3MTNjNjIxNmE5In0.eyJhdWQiOiIyIiwianRpIjoiZGJhNjMwYTRjMjFhZmU3NGFhNWU1OD"
            + "BmMGJmOWIwNDVhOGZjNjg1ZWIyMTRmYWZlNjhkMDMwZDNmN2I1OGEwODczYzUzNTcxM2M2MjE2YTkiLCJ"
            + "pYXQiOjE1Njg2MzI3MDEsIm5iZiI6MTU2ODYzMjcwMSwiZXhwIjoxNjAwMjU1MTAxLCJzdWIiOiIzMDQ2"
            + "MTAiLCJzY29wZXMiOltdfQ.BB_dmBJDg-NO72-HSqudwt3Ql4kO7uyVjx4sMTpMURpJZfRpd1-7wUjfER"
            + "Wumhp1JcQzFwiRsihKTLN_rVeLNzspcXUHtp6OTRBEPowrzhQq3tB8-mQxTL-8KV2QPOaDmYufoowtCax"
            + "bTp7ciKs7Ybs4t9XYfGQoyBurWJsspOU_ikvWHlZViHZQjs82aAIblC-XyQh94sJz0_3mDHQPcGhlpBXp"
            + "-RMi-hghGZsFS_ZhugSKvz5bKeR0bzDTui1baUoGnCFOWwSDn_tek1rSpAIdOe5WK1J2Opvjx7kjq7ycj"
            + "bDy8ZNurWhEExa8rrFRRHlzN2AwiWkwAd6BmMHI_nVhgGbqUzDg88_393nX8T8Fdrek-IU1k461MzAycs"
            + "RiKTj_vRXVyNie25v-wg3aQ9PciB-zQH1JbBM6wPmavwi-5zWD5Ab5m3FGijBviPCnN2Q-H9aBN3zPLYr"
            + "YHG305Rmq6DHc_QoC_GYISk_BbbU3_DrvnUml2ey07B0bprVKJPZWYE9LHW5A3k4-yV3eAR5rf-JcQjCi"
            + "gONs0lkcA9uu91R-n3VxmtIyOIO76SLdkiZEebV8yvIC696zrxPMzNktoDUc-SP359MZDHIvmtL2uyy3s"
            + "83n3LtgwgamZC4e6_-GmU89npACLD5hK7-lVXeVRGAKZuhTHKWuKbEqLJ8";

    public Post getStatistic(String slug) {
        Long id = getPostId(slug);
        List<Like> likes = getPostLikes(id);
        List<Repost> reposts = getPostReposts(id);
        List<Comment> comments = getPostComments(id);
        List<Mention> mentions = getPostMentions(id);

        Post post = new Post();
        post.setId(id);
        post.setSlug(slug);
        post.setLikes(likes);
        post.setReposts(reposts);
        post.setComments(comments);
        post.setMentions(mentions);
        return post;
    }

    private List<Mention> getPostMentions(Long id) {
        try (CloseableHttpClient httpClient = HttpClientBuilder.create().build()) {
            List<Mention> mentions = new ArrayList<>();
            JSONObject object;
            int page = 1;
            do {
                HttpPost request = new HttpPost("https://api.inrating.top/v1/users/posts"
                        + "/mentions/all?id=" + id + "&page=" + page++);
                request.addHeader(CONTENT_TYPE, APPLICATION_JSON);
                request.addHeader(AUTHORIZATION, TOKEN);

                HttpResponse result = httpClient.execute(request);
                String json = EntityUtils.toString(result.getEntity(), ENCODING_UTF);
                object = new JSONObject(json);
                for (int i = 0; i < object.getJSONArray("data").length(); i++) {
                    mentions.add(new Mention(object.getJSONArray("data").getJSONObject(i)
                            .getLong("id"), object.getJSONArray("data").getJSONObject(i)
                            .getString("nickname")));
                }
            } while (object.getJSONArray("data").length() >= 15);
            return mentions;
        } catch (IOException ex) {
            throw new RuntimeException("Failed to get data");
        }
    }

    private List<Comment> getPostComments(Long id) {
        try (CloseableHttpClient httpClient = HttpClientBuilder.create().build()) {
            List<Comment> comments = new ArrayList<>();
            JSONObject object;
            int page = 1;
            do {
                HttpPost request = new HttpPost("https://api.inrating.top/v1/users/posts"
                        + "/commentators/all?id=" + id + "&page=" + page++);
                request.addHeader(CONTENT_TYPE, APPLICATION_JSON);
                request.addHeader(AUTHORIZATION, TOKEN);

                HttpResponse result = httpClient.execute(request);
                String json = EntityUtils.toString(result.getEntity(), ENCODING_UTF);
                object = new JSONObject(json);

                for (int i = 0; i < object.getJSONArray("data").length(); i++) {
                    comments.add(new Comment(object.getJSONArray("data").getJSONObject(i)
                            .getLong("id"), object.getJSONArray("data").getJSONObject(i)
                            .getString("nickname")));
                }
            } while (object.getJSONArray("data").length() >= 15);
            return comments;
        } catch (IOException ex) {
            throw new RuntimeException("Failed to get data");
        }
    }

    private List<Repost> getPostReposts(Long id) {
        try (CloseableHttpClient httpClient = HttpClientBuilder.create().build()) {
            List<Repost> reposts = new ArrayList<>();
            JSONObject object;
            int page = 1;
            do {
                HttpPost request = new HttpPost("https://api.inrating.top/v1/users/posts"
                        + "/reposters/all?id=" + id + "&page=" + page++);
                request.addHeader(CONTENT_TYPE, APPLICATION_JSON);
                request.addHeader(AUTHORIZATION, TOKEN);

                HttpResponse result = httpClient.execute(request);
                String json = EntityUtils.toString(result.getEntity(), ENCODING_UTF);
                object = new JSONObject(json);

                for (int i = 0; i < object.getJSONArray("data").length(); i++) {
                    reposts.add(new Repost(object.getJSONArray("data").getJSONObject(i)
                            .getLong("id"), object.getJSONArray("data").getJSONObject(i)
                            .getString("nickname")));
                }
            } while (object.getJSONArray("data").length() >= 15);
            return reposts;
        } catch (IOException ex) {
            throw new RuntimeException("Failed to get data");
        }
    }

    private List<Like> getPostLikes(Long id) {
        try (CloseableHttpClient httpClient = HttpClientBuilder.create().build()) {
            List<Like> likes = new ArrayList<>();
            JSONObject object;
            int page = 1;
            do {
                HttpPost request = new HttpPost("https://api.inrating.top/v1/users/posts"
                        + "/likers/all?id=" + id + "&page=" + page++);
                request.addHeader(CONTENT_TYPE, APPLICATION_JSON);
                request.addHeader(AUTHORIZATION, TOKEN);

                HttpResponse result = httpClient.execute(request);
                String json = EntityUtils.toString(result.getEntity(), ENCODING_UTF);
                object = new JSONObject(json);

                for (int i = 0; i < object.getJSONArray("data").length(); i++) {
                    likes.add(new Like(object.getJSONArray("data").getJSONObject(i)
                            .getLong("id"), object.getJSONArray("data").getJSONObject(i)
                            .getString("nickname")));
                }
            } while (object.getJSONArray("data").length() >= 15);
            return likes;
        } catch (IOException ex) {
            throw new RuntimeException("Failed to get data");
        }
    }

    private Long getPostId(String slug) {
        try (CloseableHttpClient httpClient = HttpClientBuilder.create().build()) {
            HttpPost request = new HttpPost("https://api.inrating.top/v1/users/posts/get?slug="
                    + slug);
            request.addHeader(CONTENT_TYPE, APPLICATION_JSON);
            request.addHeader(AUTHORIZATION, TOKEN);

            HttpResponse result = httpClient.execute(request);
            String json = EntityUtils.toString(result.getEntity(), ENCODING_UTF);
            JSONObject object = new JSONObject(json);
            return object.getLong("id");
        } catch (IOException ex) {
            throw new RuntimeException("Failed to get data");
        }
    }
}

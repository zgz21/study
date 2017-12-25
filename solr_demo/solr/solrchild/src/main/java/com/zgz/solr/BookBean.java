package com.zgz.solr;

import org.apache.solr.client.solrj.beans.Field;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;

/**
 * @author 张光泽
 * @version v1.0
 * @project: solr
 * @description: 这里描述类的用处
 * @copyright: © 2017
 * @company:
 * @date 2017/12/21 10:48
 */
public class BookBean implements Serializable{

    private static final long  serialVersionUID =189234389L;

    @Field("id")
    private String id;

    @Field("type_s")
    private String types;

    @Field("review_dt")
    private Date reviewDate;

    @Field("stars_i")
    private Integer stars;

    @Field("author_s")
    private String author;

    @Field("comment_t")
    private String[] comments;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTypes() {
        return types;
    }

    public void setTypes(String types) {
        this.types = types;
    }

    public Date getReviewDate() {
        return reviewDate;
    }

    public void setReviewDate(Date reviewDate) {
        this.reviewDate = reviewDate;
    }

    public Integer getStars() {
        return stars;
    }

    public void setStars(Integer stars) {
        this.stars = stars;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String[] getComments() {
        return comments;
    }

    public void setComments(String[] comments) {
        this.comments = comments;
    }

    @Override
    public String toString() {
        return "BookBean{" +
                "id='" + id + '\'' +
                ", types='" + types + '\'' +
                ", reviewDate=" + reviewDate +
                ", stars=" + stars +
                ", author='" + author + '\'' +
                ", comments=" + Arrays.toString(comments) +
                '}';
    }
}

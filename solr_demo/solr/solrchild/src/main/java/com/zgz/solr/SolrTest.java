package com.zgz.solr;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.Group;
import org.apache.solr.client.solrj.response.GroupCommand;
import org.apache.solr.client.solrj.response.GroupResponse;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.params.GroupParams;
import org.apache.solr.common.params.MapSolrParams;
import org.junit.Test;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static org.apache.solr.common.params.GroupParams.GROUP;

/**
 * @author 张光泽
 * @version v1.0
 * @project: solr
 * @description: 这里描述类的用处
 * @copyright: © 2017
 * @company:
 * @date 2017/12/20 10:41
 */
public class SolrTest {

    private static Map<String,SolrClient> CLIENT_MAP = null;

    private static String URL = "http://localhost:8983/solr/core2";

    /*
    * start:
    * cd E:\solr\solr-5.5.5\bin
    * command: solr start
    *
    * stop:
    * command: solr stop -all
    *
    * create collection
    * command: solr create -c collection1
    *
    *
    **/
    
    /**
     * documnet
     {id : book1, type_s:book, title_t : "The Way of Kings", author_s : "Brandon Sanderson",
     cat_s:fantasy, pubyear_i:2010, publisher_s:Tor,
     _childDocuments_ : [
     { id: book1_c1, type_s:review, review_dt:"2015-01-03T14:30:00Z",
     stars_i:5, author_s:yonik,
     comment_t:"A great start to what looks like an epic series!"
     }
     ,
     { id: book1_c2, type_s:review, review_dt:"2014-03-15T12:00:00Z",
     stars_i:3, author_s:dan,
     comment_t:"This book was too long."
     }
     ]
     }*/

    public static SolrClient get(String url) throws Exception {
        if(CLIENT_MAP == null) {
            init();
        }
        SolrClient solrClient = CLIENT_MAP.get(url);
        if(solrClient == null) {
            throw  new Exception("solrClient is null");
        }
        return solrClient;
    }


    public static void init(){
        CLIENT_MAP = new HashMap<String,SolrClient>();
        SolrClient solrClient = new HttpSolrClient(URL);
        CLIENT_MAP.put(URL,solrClient);
    }

    @Test
    public void testQueryAll() throws IOException, SolrServerException {
        SolrClient solrClient = null;
        try {
            solrClient = get(URL);
        } catch (Exception e) {
            e.printStackTrace();
        }
        SolrQuery query = new SolrQuery();
        query.setQuery("*:*");
        query.addSort("id",SolrQuery.ORDER.desc);
        QueryResponse rsp = solrClient.query(query);
        SolrDocumentList list = rsp.getResults();

        for (int i = 0; i <list.size(); i++){
            // show all
            System.out.println(list.get(i));

            // show part
            SolrDocument doc = list.get(i);
            System.out.println(doc.get("id") + " " + doc.get("type_s"));
        }
        solrClient.close();
    }

    @Test
    public void testAdd() throws IOException, SolrServerException {
        SolrClient solrClient = null;
        try {
            solrClient = get(URL);
        } catch (Exception e) {
            e.printStackTrace();
        }


        SolrInputDocument doc = new SolrInputDocument();
        doc.addField("id","book_5");
        doc.addField("type_s","book");
        doc.addField("cat_s","fantasy");

        SolrInputDocument child = new SolrInputDocument();
        child.addField("id","book_5_1");
        child.addField("type_s","review");


        doc.addChildDocument(child);


        UpdateResponse res = solrClient.add(doc);
        System.out.println(res.getElapsedTime());

        solrClient.commit();
        solrClient.close();
    }

    @Test
    public void testUpdate() throws IOException, SolrServerException {
        SolrClient solrClient = null;
        try {
            solrClient = get(URL);
        } catch (Exception e) {
            e.printStackTrace();
        }

        SolrInputDocument doc = new SolrInputDocument();
        doc.addField("id","book_4");
        doc.addField("type_s","book");
        doc.addField("cat_s","fantasy");
        doc.addField("start_i",6);


        UpdateResponse res = solrClient.add(doc);
        System.out.println(res.getElapsedTime());

        solrClient.commit();
        solrClient.close();
    }

    @Test
    public void testDelete() throws IOException, SolrServerException {
        SolrClient solrClient = null;
        try {
            solrClient = get(URL);
        } catch (Exception e) {
            e.printStackTrace();
        }
        ArrayList idList = new ArrayList();
        idList.add("book6");
        idList.add("book_5_1");

        solrClient.deleteById(idList);
        solrClient.commit();
        solrClient.close();
    }

    @Test
    public void testDeleteByQuery() throws Exception {
        SolrClient solrClient = null;
        try {
            solrClient = get(URL);
        } catch (Exception e) {
            e.printStackTrace();
        }

        solrClient.deleteByQuery("*:*");
        solrClient.commit();
        solrClient.close();
    }

    @Test
    public void testQueryChild() throws IOException, SolrServerException {
        SolrClient solrClient = null;
        try {
            solrClient = get(URL);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Map<String,String> queryParaMap = new HashMap<String, String>();
        queryParaMap.put("q","cat_s:(fantasy OR sci-fi)");
        queryParaMap.put("fl","id,[child parentFilter=type_s:book]");
        MapSolrParams queryParam = new MapSolrParams(queryParaMap);
        QueryResponse res = solrClient.query(queryParam);

        SolrDocumentList list = res.getResults();
        for(SolrDocument doc:list){
            System.out.println(doc.get("id"));
            for(SolrDocument child:doc.getChildDocuments()){
                System.out.println(child.get("id"));
            }
        }
        solrClient.close();
    }

    @Test
    public void testAddBean() throws Exception {
        SolrClient solrClient = null;
        try {
            solrClient = get(URL);
        } catch (Exception e) {
            e.printStackTrace();
        }

        BookBean b = new BookBean();
        b.setId("book_3");
        b.setTypes("book");
        b.setAuthor("zgz");
        b.setReviewDate(new Date());
        b.setStars(5);

        String[] comment = new String[]{"Yes,very good"};
        b.setComments(comment);

        solrClient.addBean(b);
        solrClient.commit();
        solrClient.close();
    }

    @Test
    public void testQueryToBean()throws Exception{
        SolrClient solrClient = null;
        try {
            solrClient = get(URL);
        } catch (Exception e) {
            e.printStackTrace();
        }

        SolrQuery query = new SolrQuery();
        query.setQuery("id:book_3");

        QueryResponse res = solrClient.query(query);

        SolrDocumentList list = res.getResults();
        //使用DocumentObjectBinder获取
        List<BookBean> items = solrClient.getBinder().getBeans(BookBean.class,list);
        for(BookBean b:items){
            System.out.println(b.toString());
        }
    }

    @Test
    public void testGetById()throws Exception{
        SolrClient solrClient = null;
        try {
            solrClient = get(URL);
        } catch (Exception e) {
            e.printStackTrace();
        }

        ArrayList ids = new ArrayList();

        ids.add("book1_c1");
        ids.add("book1_c2");

        SolrDocumentList list = solrClient.getById(ids);
        //使用DocumentObjectBinder获取
        List<BookBean> items = solrClient.getBinder().getBeans(BookBean.class,list);
        for(BookBean b:items){
            System.out.println(b.toString());
        }
    }

    @Test
    public void testGroup()throws Exception{
        SolrClient solrClient = null;
        try {
            solrClient = get(URL);
        } catch (Exception e) {
            e.printStackTrace();
        }

        SolrQuery query = new SolrQuery();
        query.setQuery("*:*");
        query.setParam(GROUP, true);
        query.setParam(GroupParams.GROUP_FIELD, "author_s");
        query.setParam(GroupParams.GROUP_LIMIT,"100");//每组显示的个数，默认为1

        QueryResponse res = solrClient.query(query);
        GroupResponse groupResponse = res.getGroupResponse();
        Map<String, Integer> info = new HashMap<String, Integer>();
        SolrDocumentList solrDocuments = null;
        if(groupResponse != null) {
            List<GroupCommand> groupList = groupResponse.getValues();
            for(GroupCommand groupCommand : groupList) {
                List<Group> groups = groupCommand.getValues();
                for(Group group : groups) {
                    info.put(group.getGroupValue(), (int)group.getResult().getNumFound());
                    System.out.println(group.getGroupValue()+":");
                    solrDocuments =group.getResult();
                    for(SolrDocument doc:solrDocuments){
                        System.out.println(doc.toString());
                    }

                }
            }
        }

    }

    @Test
    public void testFacet()throws Exception{
        SolrClient solrClient = null;
        try {
            solrClient = get(URL);
        } catch (Exception e) {
            e.printStackTrace();
        }

        SolrQuery query = new SolrQuery();
        query.setQuery("*:*");
        query.setFacet(true);
        query.addFacetField("author_s");

        QueryResponse res = solrClient.query(query);
        List<FacetField> facets = res.getFacetFields();
        for (FacetField facet : facets) {
            System.out.println(facet.getName());
            System.out.println("----------------");
            List<FacetField.Count> counts = facet.getValues();
            for (FacetField.Count count : counts) {
                System.out.println(count.getName() + ":" + count.getCount());
            }
        }
    }

    @Test
    public void testHighlight()throws Exception{
        SolrClient solrClient = null;
        try {
            solrClient = get(URL);
        } catch (Exception e) {
            e.printStackTrace();
        }

        SolrQuery query = new SolrQuery();
        // must be detail query ,query=*:* that won't be valid.
        // 必须是具体一点的查询，如author_s:*，如果是*:*全局查询，好像高亮不起效果。
        query.setQuery("author_s:*");
        query.setHighlight(true);
        query.addHighlightField("title_s");
        query.setHighlightSimplePre("<font color='red'>");
        query.setHighlightSimplePost("</font>");


        QueryResponse res = solrClient.query(query);
        SolrDocumentList list = res.getResults();

        Map<String,Map<String,List<String>>> highlightMap=res.getHighlighting();

        String tmpId;
        BookBean book;
        List<BookBean> bookList = new ArrayList<BookBean>();
        SimpleDateFormat format = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", Locale.UK);
        String[] commentStr = new String[10];
        Collection cc;
        for(SolrDocument doc:list){
            book = new BookBean();
            tmpId=doc.getFieldValue("id").toString();
            book.setId(tmpId);
            cc = doc.getFieldValues("comment_t");
            if(cc!=null && cc.size()>0){
                book.setComments((String[]) cc.toArray(commentStr));
            }

            if(doc.getFieldValue("stars_i")!=null){
                book.setStars(Integer.valueOf(doc.getFieldValue("stars_i").toString()));
            }

            if(doc.getFieldValue("review_dt")!=null){
                book.setReviewDate(format.parse(doc.getFieldValue("review_dt").toString()));
            }


            List<String> typeList=highlightMap.get(tmpId).get("title_s");

            if(typeList!=null && typeList.size()>0){
                book.setTypes(typeList.get(0));
            }else{
                if(doc.getFieldValue("title_s")!=null){
                    book.setTypes(doc.getFieldValue("title_s").toString());
                }
            }
            bookList.add(book);
        }

        System.out.println(bookList.toString());

    }

    @Test
    public void testModifier()throws Exception{
        SolrClient solrClient = null;
        try {
            solrClient = get(URL);
        } catch (Exception e) {
            e.printStackTrace();
        }

        SolrInputDocument doc = new SolrInputDocument();
        Map<String, String> partialUpdate = new HashMap<String, String>();
        partialUpdate.put("set", "fantasy_s");
        doc.addField("id", "book1");
        doc.addField("cat_s", partialUpdate);


        Map<String,Long> price=new HashMap<String, Long>();
        price.put("inc",3L);
        doc.addField("sequence_i",price);


        solrClient.add(doc);
        solrClient.commit();
        solrClient.close();

    }



}

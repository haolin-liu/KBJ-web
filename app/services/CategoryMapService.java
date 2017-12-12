package services;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.entities.KbjCategory;
import models.entities.MallCategory;
import models.entities.form.BindCategory;
import play.data.Form;
import play.data.FormFactory;
import play.libs.Json;
import repository.CategoryMapRepo;
import repository.DatabaseExecutionContext;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletionStage;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.concurrent.CompletableFuture.supplyAsync;

/**
 * This service created to get datas from entity
 *  @author yue-yao
 *  @date 2017/11/24
 */
public class CategoryMapService {

    private final CategoryMapRepo categoryMapRepo;
    private final FormFactory formFactory;
    private final DatabaseExecutionContext executionContext;

    @Inject
    public CategoryMapService(FormFactory formFactory,
                              CategoryMapRepo categoryMapRepo,
                              DatabaseExecutionContext executionContext) {
        this.formFactory = formFactory;
        this.categoryMapRepo = categoryMapRepo;
        this.executionContext = executionContext;

    }

    /**
     * get root category datas
     * @return
     */
    public CompletionStage<List<KbjCategory>> getKbjRootCates() {
        return supplyAsync(() -> {
            return categoryMapRepo.findRootCates();
        }, executionContext);
    }

    /**
     * get leaf category datas
     * @param cateRootId
     * @return
     */
    public CompletionStage<ArrayNode> getKbjLeafCates(String cateRootId) {
        return supplyAsync(() -> {
            List<KbjCategory> leafCates = categoryMapRepo.findLeafCates(cateRootId);
            ArrayNode catesJson = Json.newArray();
            for(KbjCategory kbjChildCate: leafCates) {
                ObjectNode cateJson = Json.newObject();

                cateJson.put("id", kbjChildCate.id);
                cateJson.put("name", kbjChildCate.name);
                catesJson.add(cateJson);
            }
            return catesJson;
        }, executionContext);
    }

    /**
     * get categories
     * @param categoryForm
     * @return
     */
    public CompletionStage<List<BindCategory>> getMallCategory(Form<BindCategory> categoryForm) {
        BindCategory bindCategory = categoryForm.get();
        String keyWordForm = bindCategory.keyWord;

        ArrayList<String> keyWord = editKeyWord(keyWordForm);

        return supplyAsync(() -> {
//            List<MallCategory> mallCates = categoryMapRepo.find(mall, kw);
//            for(MallCategory mallCate: mallCates) {
//                System.out.println(mallCate.tag);
//            }
            return categoryMapRepo.find(keyWord, bindCategory);
        }, executionContext);
    }

    /**
     * 绑定更新品类并返回检索list
     * @param data
     * @param categoryForm
     * @return
     */
    public CompletionStage<List<BindCategory>> edit(Map<String, String[]> data, Form<BindCategory> categoryForm) {
        //处理keyWords
        BindCategory bindCategory = categoryForm.get();
        String keyWordForm = bindCategory.keyWord;
        Long kbjCateId = Long.valueOf(bindCategory.leafCate);

        ArrayList<String> keyWord = editKeyWord(keyWordForm);

        //处理绑定更新
        return supplyAsync(() -> {
            for (int i = 0; i < data.get("chgFlg").length; i++) {
                if (("1").equals(data.get("chgFlg")[i])) {
                    if (("0").equals(data.get("bindFlg")[i])) {
                        categoryMapRepo.delete(Long.valueOf(data.get("mapId")[i]));
                    } else {
                        categoryMapRepo.insert(kbjCateId, Long.valueOf(data.get("mallCateId")[i]));
                    }
                }
            }
            return Optional.empty();
        }, executionContext).thenApplyAsync(v -> {
            return categoryMapRepo.find(keyWord, bindCategory);
        }, executionContext);
    }

    /**
     * 处理前端传过来的“检索关键字”
     * @param keyWordForm
     * @return
     */
    private ArrayList<String> editKeyWord(String keyWordForm) {
        String regEx="[`~!@#$%^&*()+=|{}':;'\\[\\].,<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(keyWordForm);
        String keyWordTemp = m.replaceAll(" ");

        String[] splitBySpaces = keyWordTemp.split("\\s");
        ArrayList<String> keyWord = new ArrayList<String>();

        for (int i=0; i < splitBySpaces.length; i++) {
            if(!splitBySpaces[i].equals("")) {
                keyWord.add(splitBySpaces[i]);
            }
        }
        return keyWord;
    }
}

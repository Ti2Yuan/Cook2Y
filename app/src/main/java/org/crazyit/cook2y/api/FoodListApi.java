package org.crazyit.cook2y.api;

/**
 * Created by chenti on 2016/4/22.
 */
public class FoodListApi {

    public static String food_list_url = "http://www.tngou.net/api/cook/list";

    //可以再图片后面添加宽度和高度，如：http://tnfs.tngou.net/image/top/default.jpg_180x120
    //如果加载图片时用此url前缀，则加载图片时有问题，建议使用下一个url
    public static String food_img_url_1 = "http://tnfs.tngou.net/image";

    public static String food_img_url_2 = "http://tnfs.tngou.net/img";
}

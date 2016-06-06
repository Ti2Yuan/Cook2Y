package org.crazyit.cook2y.Collections;

import java.util.Set;

/**
 * Created by chenti on 2016/5/15.
 */
public class CollectionsIDSet {

    public static Set<Integer> collectionsId; //存储收藏过的食谱id

    public static Set<Integer> getCollectionsId() {
        return collectionsId;
    }

    public static void setCollectionsId(Set<Integer> collectionsId) {
        CollectionsIDSet.collectionsId = collectionsId;
    }
}

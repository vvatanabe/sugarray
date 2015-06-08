/**
 *
 */
package net.ultramagnetic.sugarray.util;

import java.util.Collection;
import java.util.Map;

/**
 * @author watanayu
 */
public class ContainerUtils {

    private ContainerUtils() {
        ;
    }

    /**
     * Collectionの引数がnullまたは空であればtrueを返します
     *
     * @param collection
     * @return
     */
    @SuppressWarnings("rawtypes")
    public static boolean isEmpty(Collection collection) {
        return collection == null || collection.size() == 0;
    }

    /**
     * Collectionの引数がnullでも空でもなければtrueを返却します
     *
     * @param collections
     * @return
     */
    public static boolean isNotEmpty(Collection<?> collection) {
        return !isEmpty(collection);
    }

    /**
     * Mapの引数がnullまたは空であればtrueを返します
     *
     * @param map
     * @return
     */
    @SuppressWarnings("rawtypes")
    public static boolean isEmpty(Map map) {
        return map == null || map.size() == 0;
    }

    /**
     * Mapの引数がnullでも空でもなければtrueを返却します
     *
     * @param map
     * @return
     */
    public static boolean isNotEmpty(Map<?, ?> map) {
        return !isEmpty(map);
    }
}

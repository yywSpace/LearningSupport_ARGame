package com.example.learningsupport_argame.Course;

import com.contrarywind.interfaces.IPickerViewData;

import java.util.List;

public class JsonBeanJie implements IPickerViewData {

    /**
     * name : 省份
     * city : [{"name":"北京市","area":["东城区","西城区","崇文区","宣武区","朝阳区"]}]
     */
    private String name;
    private List<JieBean> jie;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<JieBean> getJieList() {
        return jie;
    }

    public void setJieList(List<JieBean> jie) {
        this.jie = jie;
    }

    // 实现 IPickerViewData 接口，
    // 这个用来显示在PickerView上面的字符串，
    // PickerView会通过IPickerViewData获取getPickerViewText方法显示出来。
    @Override
    public String getPickerViewText() {
        return this.name;
    }

    public static class JieBean {
        /**
         * name : 城市
         * area : ["东城区","西城区","崇文区","昌平区"]
         */
        private String name;
        private List<String> endjie;

        public List<String> getEndjie() {
            return endjie;
        }

        public void setEndjie(List<String> endjie) {
            this.endjie = endjie;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

//        public List<String> getArea() {
//            return endjie;
//        }
//
//        public void setArea(List<String> area) {
//            this.endjie = area;
//        }
    }

}

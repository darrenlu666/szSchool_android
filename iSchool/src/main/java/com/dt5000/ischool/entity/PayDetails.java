package com.dt5000.ischool.entity;

import java.util.List;

/**
 * Created by weimy on 2018/1/3.
 */

public class PayDetails {


    /**
     * appstoreLink : "www.baidu.com"
     * hasNext : 1
     * highestVersion : 1.1.0
     * lowestVersion : 1.1.0
     * memo :
     * orders : [{"endDate":{"date":1,"day":2,"hours":10,"minutes":28,"month":2,"nanos":0,"seconds":13,"time":1456799293000,"timezoneOffset":-480,"year":116},"goodsName":"点通测试","goodsTime":3,"orderId":27995,"orderNo":"090111463914804","payTime":{"date":1,"day":4,"hours":11,"minutes":46,"month":8,"nanos":0,"seconds":39,"time":1472701599000,"timezoneOffset":-480,"year":116},"price":"0.01","status":1},{"endDate":{"date":30,"day":1,"hours":10,"minutes":29,"month":10,"nanos":0,"seconds":13,"time":1448850553000,"timezoneOffset":-480,"year":115},"goodsName":"测试","goodsTime":3,"orderId":16850,"orderNo":"092810273319110","payTime":{"date":28,"day":1,"hours":10,"minutes":29,"month":8,"nanos":0,"seconds":13,"time":1443407353000,"timezoneOffset":-480,"year":115},"price":"1","status":1},{"endDate":{"date":31,"day":3,"hours":11,"minutes":8,"month":11,"nanos":0,"seconds":31,"time":1419995311000,"timezoneOffset":-480,"year":114},"goodsName":"一份套餐","goodsTime":1,"orderId":7198,"orderNo":"1223110730-9894","payTime":{"date":23,"day":2,"hours":11,"minutes":8,"month":11,"nanos":0,"seconds":31,"time":1419304111000,"timezoneOffset":-480,"year":114},"price":"0.01","status":1}]
     * resultStatus : 200
     */

    private String appstoreLink;
    private int hasNext;
    private String highestVersion;
    private String lowestVersion;
    private String memo;
    private String resultStatus;
    private List<OrdersBean> orders;

    public String getAppstoreLink() {
        return appstoreLink;
    }

    public void setAppstoreLink(String appstoreLink) {
        this.appstoreLink = appstoreLink;
    }

    public int getHasNext() {
        return hasNext;
    }

    public void setHasNext(int hasNext) {
        this.hasNext = hasNext;
    }

    public String getHighestVersion() {
        return highestVersion;
    }

    public void setHighestVersion(String highestVersion) {
        this.highestVersion = highestVersion;
    }

    public String getLowestVersion() {
        return lowestVersion;
    }

    public void setLowestVersion(String lowestVersion) {
        this.lowestVersion = lowestVersion;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public String getResultStatus() {
        return resultStatus;
    }

    public void setResultStatus(String resultStatus) {
        this.resultStatus = resultStatus;
    }

    public List<OrdersBean> getOrders() {
        return orders;
    }

    public void setOrders(List<OrdersBean> orders) {
        this.orders = orders;
    }


    public static class OrdersBean {
        /**
         * endDate : {"date":1,"day":2,"hours":10,"minutes":28,"month":2,"nanos":0,"seconds":13,"time":1456799293000,"timezoneOffset":-480,"year":116}
         * goodsName : 点通测试
         * goodsTime : 3
         * orderId : 27995
         * orderNo : 090111463914804
         * payTime : {"date":1,"day":4,"hours":11,"minutes":46,"month":8,"nanos":0,"seconds":39,"time":1472701599000,"timezoneOffset":-480,"year":116}
         * price : 0.01
         * status : 1
         */

        private EndDateBean endDate;
        private String goodsId;
        private String goodsName;
        private int goodsTime;
        private int orderId;
        private String orderNo;
        private PayTimeBean payTime;
        private String price;
        private int status;

        public EndDateBean getEndDate() {
            return endDate;
        }

        public void setEndDate(EndDateBean endDate) {
            this.endDate = endDate;
        }

        public String getGoodsName() {
            return goodsName;
        }

        public void setGoodsName(String goodsName) {
            this.goodsName = goodsName;
        }

        public int getGoodsTime() {
            return goodsTime;
        }

        public void setGoodsTime(int goodsTime) {
            this.goodsTime = goodsTime;
        }

        public int getOrderId() {
            return orderId;
        }

        public void setOrderId(int orderId) {
            this.orderId = orderId;
        }

        public String getOrderNo() {
            return orderNo;
        }

        public void setOrderNo(String orderNo) {
            this.orderNo = orderNo;
        }

        public PayTimeBean getPayTime() {
            return payTime;
        }

        public void setPayTime(PayTimeBean payTime) {
            this.payTime = payTime;
        }

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public String getGoodsId() {
            return goodsId;
        }

        public void setGoodsId(String goodsId) {
            this.goodsId = goodsId;
        }

        public static class EndDateBean {
            /**
             * date : 1
             * day : 2
             * hours : 10
             * minutes : 28
             * month : 2
             * nanos : 0
             * seconds : 13
             * time : 1456799293000
             * timezoneOffset : -480
             * year : 116
             */

            private int date;
            private int day;
            private int hours;
            private int minutes;
            private int month;
            private int nanos;
            private int seconds;
            private long time;
            private int timezoneOffset;
            private int year;

            public int getDate() {
                return date;
            }

            public void setDate(int date) {
                this.date = date;
            }

            public int getDay() {
                return day;
            }

            public void setDay(int day) {
                this.day = day;
            }

            public int getHours() {
                return hours;
            }

            public void setHours(int hours) {
                this.hours = hours;
            }

            public int getMinutes() {
                return minutes;
            }

            public void setMinutes(int minutes) {
                this.minutes = minutes;
            }

            public int getMonth() {
                return month;
            }

            public void setMonth(int month) {
                this.month = month;
            }

            public int getNanos() {
                return nanos;
            }

            public void setNanos(int nanos) {
                this.nanos = nanos;
            }

            public int getSeconds() {
                return seconds;
            }

            public void setSeconds(int seconds) {
                this.seconds = seconds;
            }

            public long getTime() {
                return time;
            }

            public void setTime(long time) {
                this.time = time;
            }

            public int getTimezoneOffset() {
                return timezoneOffset;
            }

            public void setTimezoneOffset(int timezoneOffset) {
                this.timezoneOffset = timezoneOffset;
            }

            public int getYear() {
                return year;
            }

            public void setYear(int year) {
                this.year = year;
            }
        }

        public static class PayTimeBean {
            /**
             * date : 1
             * day : 4
             * hours : 11
             * minutes : 46
             * month : 8
             * nanos : 0
             * seconds : 39
             * time : 1472701599000
             * timezoneOffset : -480
             * year : 116
             */

            private int date;
            private int day;
            private int hours;
            private int minutes;
            private int month;
            private int nanos;
            private int seconds;
            private long time;
            private int timezoneOffset;
            private int year;

            public int getDate() {
                return date;
            }

            public void setDate(int date) {
                this.date = date;
            }

            public int getDay() {
                return day;
            }

            public void setDay(int day) {
                this.day = day;
            }

            public int getHours() {
                return hours;
            }

            public void setHours(int hours) {
                this.hours = hours;
            }

            public int getMinutes() {
                return minutes;
            }

            public void setMinutes(int minutes) {
                this.minutes = minutes;
            }

            public int getMonth() {
                return month;
            }

            public void setMonth(int month) {
                this.month = month;
            }

            public int getNanos() {
                return nanos;
            }

            public void setNanos(int nanos) {
                this.nanos = nanos;
            }

            public int getSeconds() {
                return seconds;
            }

            public void setSeconds(int seconds) {
                this.seconds = seconds;
            }

            public long getTime() {
                return time;
            }

            public void setTime(long time) {
                this.time = time;
            }

            public int getTimezoneOffset() {
                return timezoneOffset;
            }

            public void setTimezoneOffset(int timezoneOffset) {
                this.timezoneOffset = timezoneOffset;
            }

            public int getYear() {
                return year;
            }

            public void setYear(int year) {
                this.year = year;
            }
        }
    }
}

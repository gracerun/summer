package com.test.message.web;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class BaoxiaoTest {

    @Test
    public void baoxiaoTest() {
        String s = "/Users/yaodan/工作文档/4月份报销明细/发票";
        final File file = new File(s);
        final File[] listFiles = file.listFiles((dir, name) ->
                name.endsWith(".pdf")
                        || name.endsWith(".jpg")
        );

        final List<Bill> bills = Stream.of(listFiles).map(e -> {
            log.info(e.getName());
            return e.getName();
        }).map(e -> e.substring(0, e.lastIndexOf("."))).map(e -> {
            final String[] split = e.split("：", 2);
            final Bill bill = new Bill();
            if ("餐费".equals(split[0])) {
                final String[] split1 = split[1].split("-", 2);
                bill.setM(Double.parseDouble(split1[1].replace("元", "")));
                fmtDate(split1[0], bill);
            } else if ("高铁".equals(split[0])) {
                final String[] split1 = split[1].split("-", 3);
                bill.setM(Double.parseDouble(split1[1].replace("元", "")));
                fmtDate(split1[0], bill);
            } else if ("住宿费".equals(split[0])) {
                final String[] split1 = split[1].split("-", 3);
                bill.setM(Double.parseDouble(split1[2].replace("元", "")));
                fmtDate(split1[0], bill);
            }
            bill.setMm(bill.getM());
            bill.setRemark(e);
            return bill;
        }).sorted(Comparator.comparing(Bill::getOrder)).collect(Collectors.toList());

        bills.forEach(e -> {
            System.out.println(e.getDate() + "\t\t-\t" + e.getM() + "\t" + e.getRemark() + "\t" + e.getMm());
        });

        String fileName = "/Users/yaodan/工作文档/4月份报销明细/报销明细.xlsx";
        ExcelWriter excelWriter = EasyExcel.write(fileName, Bill.class).build();
        WriteSheet writeSheet = EasyExcel.writerSheet("模板").build();
        excelWriter.write(bills, writeSheet);
        excelWriter.finish();
    }

    private void fmtDate(String str, Bill bill) {
        final DateTime d = DateTime.parse(str, DateTimeFormat.forPattern("mm月dd日"));
        final String ymd = "2022年" + str;
        bill.setDate(ymd);
        bill.setOrder(d.toDate());
    }

    @Getter
    @Setter
    class Bill {
        private Date order;
        private String date;
        private double m;
        private String remark;
        private double mm;

        @Override
        public String toString() {
            return new ToStringBuilder(this)
                    .append("date", date)
                    .append("m", m)
                    .append("remark", remark)
                    .append("mm", mm)
                    .toString();
        }
    }

}
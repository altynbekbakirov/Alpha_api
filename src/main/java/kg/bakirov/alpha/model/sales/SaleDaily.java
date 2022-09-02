package kg.bakirov.alpha.model.sales;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SaleDaily {
    private String date;
    private Double net;
    private Double net_usd;
    private Double ret_total;
    private Double ret_total_usd;
    private int trCode;

    @Override
    public String toString() {
        return "SaleDaily{" +
                "date='" + date + '\'' +
                ", trCode=" + trCode +
                ", netTotal=" + net +
                ", reportNet=" + net_usd +
                ", netReturn=" + ret_total +
                ", reportReturn=" + ret_total_usd +
                '}';
    }
}

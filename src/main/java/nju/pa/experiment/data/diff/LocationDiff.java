package nju.pa.experiment.data.diff;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The output of diff. Record the difference between mutants.
 *
 * @author QRX
 * @email QRXwzx@outlook.com
 * @date 2020-05-31
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class LocationDiff {

    /**
     * Preserve the index of MutantLocation. Only diff mutants which have the
     * same index.
     */
    @JSONField
    private String index;

    /**
     * Record if these field changed, formulation is like follows:
     *
     * [one]-[two]
     */
    @JSONField(ordinal = 1)
    private String diffStatus;

    /**
     * True if diff is show as the given formulation.
     */
    @JSONField(ordinal = 2)
    private Boolean isChanged;

    @JSONField(ordinal = 3)
    private Boolean isDeleted;

    @JSONField(ordinal = 4)
    private Boolean isAdded;


}

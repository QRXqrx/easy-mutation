package nju.pa.experiment.data;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * This class indicate parse result. Can be transferred into json, xml formats
 *
 * @author QRX
 * @email QRXwzx@outlook.com
 * @date 2020-05-25
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class MutationResult {

    /**
     * Record project folder name, for locating parsed projects.
     * Get through parameter passing.
     *
     * 2020-05-30
     * For simplicity, update to directly record report directory
     * path instead.
     *
     */
    @JSONField // default is 0
    private String reportDir;
//    private String projectFolder;

    /**
     * Record the number of mutated classes.
     * Get from summary index.html.
     */
    @JSONField(ordinal = 1)
    private Integer numberOfClasses;

    @JSONField(ordinal = 2)
    private String coveragePercentage;

    /**
     * A fraction indicates mutation coverage rate. Like: 185/201
     */
    @JSONField(ordinal = 3)
    private String coverageRatio;

    /**
     * Record parsing result for each mutated class. Grouped by Class.
     */
    @JSONField(ordinal = 4)
    private List<MutatedClassInfo> mutatedClassInfos;


}

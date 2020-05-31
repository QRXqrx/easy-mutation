package nju.pa.experiment.data.mutation;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Represent parsing result of a pitest report of a single class.
 *
 * @author QRX
 * @email QRXwzx@outlook.com
 * @date 2020-05-25
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class MutatedFileInfo {

    @JSONField
    private String packageInfo;

    @JSONField(ordinal = 1)
    private String fileName;

    /**
     * Record the number of generated mutants of this class.
     */
    @JSONField(ordinal = 2)
    private Integer numberOfMutants;

    /**
     * Record the status of mutant of this class according to pitest report.
     */
    @JSONField(ordinal = 3)
    List<MutantInfo> mutantInfos;


}

package nju.pa.experiment.data.mutation;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Record the status of each mutant.
 *
 * @author QRX
 * @email QRXwzx@outlook.com
 * @date 2020-05-25
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class MutantInfo {
    /**
     * Mutated location.
     */
    @JSONField
    private Integer lineNumber;

    /**
     * Source code of the line before mutation.
     */
    @JSONField(ordinal = 1)
    private String sourceCode;

    /**
     * Record whether this mutant has been killed. KILLED or SURVIVED
     */
    @JSONField(ordinal = 2)
    private String status;


    /**
     * Description of mutation operation.
     */
    @JSONField(ordinal = 3)
    private String description;


    /**
     * Killed by which test case.
     */
    private String killedBy;

}

package nju.pa.experiment.data.diff;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nju.pa.experiment.data.mutation.MutantInfo;

/**
 * This class is defined to prepare for diff analysis, each instance
 * represent a unique mutant.
 *
 * @author QRX
 * @email QRXwzx@outlook.com
 * @date 2020-05-31
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class MutantLocation {

    /**
     * Uniquely mark a mutant, formulation is as follows:
     *
     * packageInfo_fileName_lineNumber_description
     *
     */
    private String index;

    private String status;


    public MutantLocation(String packageInfo, String fileName, MutantInfo mutantInfo) {
        this.index = generateIndex(
                packageInfo, fileName, String.valueOf(mutantInfo.getLineNumber()), mutantInfo.getDescription());
        this.status = mutantInfo.getStatus();
    }

    private String generateIndex(String...strings) {
        StringBuilder indexBuider = new StringBuilder(100);
        for(int i = 0 ; i < strings.length ; i++) {
            indexBuider.append("[").append(strings[i]).append("]");
            if(i < strings.length - 1)
                indexBuider.append("_");
        }
        return indexBuider.toString();
    }

    public boolean sameTo(MutantLocation otherLocation) {
        return this.index.equals(otherLocation.getIndex());
    }

}

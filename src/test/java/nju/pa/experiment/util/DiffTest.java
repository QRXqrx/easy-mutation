package nju.pa.experiment.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import nju.pa.experiment.data.diff.LocationDiff;
import nju.pa.experiment.data.diff.MutantLocation;
import nju.pa.experiment.data.mutation.MutationResult;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author QRX
 * @email QRXwzx@outlook.com
 * @date 2020-05-31
 */
public class DiffTest {

    private String jptJsonPath = "material/json/JPT-RBT-mutation.json";
    private String jsonPath = "material/json/RBT-mutation.json";

    @Test
    public void testDiff() {
        try {
            String jsonContent1 = IOUtil.readAllcontent(jsonPath);
            String jsonContent2 = IOUtil.readAllcontent(jptJsonPath);
            MutationResult mutationResult1 = JSON.parseObject(jsonContent1, MutationResult.class);
            MutationResult mutationResult2 = JSON.parseObject(jsonContent2, MutationResult.class);

            List<MutantLocation> locations1 = MutationDiffUtil.mutationResultToMutantLocations(mutationResult1);
            List<MutantLocation> locations2 = MutationDiffUtil.mutationResultToMutantLocations(mutationResult2);

            List<LocationDiff> diffs = MutationDiffUtil.diff(locations1, locations2);


            System.out.println(JSON.toJSONString(diffs, SerializerFeature.PrettyFormat));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testDeletedAndAdded() {
        try {
            String jsonContent1 = IOUtil.readAllcontent(jsonPath);
            String jsonContent2 = IOUtil.readAllcontent(jptJsonPath);
            MutationResult mutationResult1 = JSON.parseObject(jsonContent1, MutationResult.class);
            MutationResult mutationResult2 = JSON.parseObject(jsonContent2, MutationResult.class);

            List<MutantLocation> locations1 = MutationDiffUtil.mutationResultToMutantLocations(mutationResult1);
            List<MutantLocation> locations2 = MutationDiffUtil.mutationResultToMutantLocations(mutationResult2);

            for (MutantLocation location : locations1)
                if(!locations2.contains(location))
                    System.out.println("deleted:" + location);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testGenerateLocations() {
        try {
            String jsonContent = IOUtil.readAllcontent(jsonPath);
            MutationResult mutationResult = JSON.parseObject(jsonContent, MutationResult.class);

            List<MutantLocation> locations = MutationDiffUtil.mutationResultToMutantLocations(mutationResult);
            System.out.println(locations.size());

            List<MutantLocation> distinctLocs = locations.stream().distinct().collect(Collectors.toList());
            System.out.println(distinctLocs.size());
            System.out.println(distinctLocs);


        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

package sia.tacos.data;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import sia.tacos.Taco;
import sia.tacos.Ingredient;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;


@Repository
public class JdbcTacoRepository implements TacoRepository {

    private SimpleJdbcInsert tacoInserter;
    private SimpleJdbcInsert tacoIngredientInserter;
    private ObjectMapper objectMapper;

    public JdbcTacoRepository(JdbcTemplate jdbc) {
        this.tacoInserter = new SimpleJdbcInsert(jdbc)
                .withTableName("Taco")
                .usingGeneratedKeyColumns("id");

        this.tacoIngredientInserter = new SimpleJdbcInsert(jdbc)
                .withTableName("Taco_Ingredients");

        this.objectMapper = new ObjectMapper();
    }

    @Override
    public Taco save(Taco taco) {
        taco.setCreatedAt(new Date());
        long tacoId = saveTacoInfo(taco);
        taco.setId(tacoId);
        for(Ingredient ingredient : taco.getIngredients()){
            saveIngredientToTaco(ingredient, tacoId);
        }
        return taco;
    }

    private long saveTacoInfo(Taco taco) {
        Map<String, Object> values = objectMapper.convertValue(taco, Map.class);
        values.put("createdAt", taco.getCreatedAt());
        long tacoId = tacoInserter.executeAndReturnKey(values).longValue();

        return tacoId;
    }

    private void saveIngredientToTaco(Ingredient ingredient, long tacoId) {
        Map<String, Object> values = new HashMap<>();
        values.put("taco", tacoId);
        values.put("ingredient", ingredient.getId());
        tacoIngredientInserter.execute(values);
    }
}

package velocitekProStartAnalyzer;

import java.util.List;


public interface PointDao {
     
 /*   public void insert(PointDao person);*/
    public List<PointDto> select();
    public void insert(PointDto pointDto);
    public void deleteVacuum();
    public void delete(int id);
}
package eu.flare.config.seed;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public abstract class DataSeeder {

    private DataSeeder nextSeeder;
    protected final JpaRepository repository;

    public DataSeeder(JpaRepository repository) {
        this.repository = repository;
    }

    public void setNextSeeder(DataSeeder nextSeeder) {
        this.nextSeeder = nextSeeder;
    }

    public void seedData(List<String> data) {
        if (!data.isEmpty()) {
            createDataIfNotExists(data);
        }
        if (nextSeeder != null) {
            nextSeeder.seedData(data);
        }
    }

    public abstract void createDataIfNotExists(List<String> data);
}

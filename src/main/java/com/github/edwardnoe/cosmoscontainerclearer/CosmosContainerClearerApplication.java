package com.github.edwardnoe.cosmoscontainerclearer;

import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import com.azure.cosmos.CosmosClient;
import com.azure.cosmos.CosmosClientBuilder;
import com.azure.cosmos.CosmosContainer;
import com.azure.cosmos.CosmosDatabase;
import com.azure.cosmos.models.CosmosItemRequestOptions;
import com.azure.cosmos.models.CosmosQueryRequestOptions;
import com.azure.cosmos.models.PartitionKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CosmosContainerClearerApplication implements CommandLineRunner {

	private CosmosClient client;

	public CosmosContainerClearerApplication(@Value("${cosmos.uri}") String uri, @Value("${cosmos.key}") String key) {
		client = new CosmosClientBuilder().endpoint(uri).key(key).buildClient();
	}

	public static void main(String[] args) {
		SpringApplication.run(CosmosContainerClearerApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		Scanner scanner = new Scanner(System.in);

		System.out.println("Input database name: ");
		String databaseName = scanner.nextLine();
		System.out.println("Input container name: ");
		String containerName = scanner.nextLine();
		System.out.println("Input container partition key: ");
		String containerPartitionKey = scanner.nextLine();
		System.out.println("Input WHERE clause (Press enter to skip and delete all documents): ");
		String whereClause = scanner.nextLine();

		CosmosDatabase database = client.getDatabase(databaseName);
        CosmosContainer container = database.getContainer(containerName);

		List<PartitionKeyIdPair> documentsToDelete = container.queryItems(
            "SELECT c." + containerPartitionKey + 
            " AS partitionKey, c.id FROM c " 
            + whereClause, 
            new CosmosQueryRequestOptions(), 
            PartitionKeyIdPair.class)
        .stream()
        .collect(Collectors.toList());

		System.out.println("This will delete " + documentsToDelete.size() + " documents in " + containerName + ". Press enter to confirm.");
		scanner.nextLine();
		scanner.close();

        for (PartitionKeyIdPair documentToDelete : documentsToDelete) {
            container.deleteItem(documentToDelete.getId(), new PartitionKey(documentToDelete.getPartitionKey()), new CosmosItemRequestOptions());
        }

		System.out.println("Deleted " + documentsToDelete.size() + " documents.");
		System.exit(0);
	}
}

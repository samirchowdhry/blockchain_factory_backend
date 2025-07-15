package com.example.blockchainfactory.Service;

import org.hyperledger.fabric.gateway.*;
import org.json.JSONArray;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeoutException;

@Service
public class BlockchainService {

    private Gateway gateway;
    private Network network;
    private Contract contract;

    public BlockchainService() throws Exception {
        Path walletPath = Paths.get(getClass().getClassLoader().getResource("wallet").toURI());
        Wallet wallet = Wallets.newFileSystemWallet(walletPath);

            Path certPath = Paths.get(getClass().getClassLoader().getResource("wallet/user1/cert.pem").toURI());
            Path keyPath = Paths.get(getClass().getClassLoader().getResource("wallet/user1/key.pem").toURI());

            X509Certificate certificate = Identities.readX509Certificate(Files.newBufferedReader(certPath));
            PrivateKey privateKey = Identities.readPrivateKey(Files.newBufferedReader(keyPath));

            X509Identity identity = Identities.newX509Identity("Org1MSP", certificate, privateKey);
            wallet.put("user1", identity);

        Path networkConfigPath = Paths.get(getClass().getClassLoader().getResource("connection-org1.yaml").toURI());

        Gateway.Builder builder = Gateway.createBuilder()
                .identity(wallet, "user1")
                .networkConfig(networkConfigPath)
                .discovery(false);

        gateway = builder.connect();

        network = gateway.getNetwork("mychannel");
        contract = network.getContract("basic");
    }

    public void createPdf(String assetID, String color, int size, String owner, int appraisedValue)
            throws ContractException, InterruptedException, TimeoutException {
        // Convert int arguments to strings
        String sizeStr = String.valueOf(size);
        String appraisedValueStr = String.valueOf(appraisedValue);
        contract.submitTransaction("CreateAsset", assetID, color, sizeStr, owner, appraisedValueStr);
    }

    public String readPdf(String assetId) throws Exception {

        byte[] hashExists = contract.evaluateTransaction("AssetExists", assetId);

        if (new String(hashExists).equals("false")) {
            return "Asset does not exist";
        }

        byte[] result = contract.evaluateTransaction("ReadAsset", assetId);

        return new String(result);
    }

    public JSONArray getAllPdf() throws Exception {
        byte[] result = contract.evaluateTransaction("GetAllAssets");
        String jsonString = new String(result);
        return new JSONArray(jsonString);
    }

    public String deletePdf(String assetId) throws Exception {
        byte[] result = contract.submitTransaction("DeleteAsset", assetId);
        return new String(result); // usually returns confirmation or empty
    }

    public void putAsset(String assetID, String color, int size, String owner, int appraisedValue) throws Exception {

        contract.submitTransaction("UpdateAsset", assetID, color, String.valueOf(size), owner, String.valueOf(appraisedValue));
    }


    public void close() {
        if (gateway != null) {
            gateway.close();
        }
    }
}

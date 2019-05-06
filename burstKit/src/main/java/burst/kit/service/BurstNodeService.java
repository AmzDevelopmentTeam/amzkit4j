package burst.kit.service;

import burst.kit.entity.*;
import burst.kit.entity.response.*;
import burst.kit.service.impl.BurstNodeServiceImpl;
import burst.kit.service.impl.DefaultSchedulerAssigner;
import burst.kit.util.SchedulerAssigner;
import io.reactivex.Observable;
import io.reactivex.Single;

import java.util.Map;
import java.util.Set;

public interface BurstNodeService {
    /**
     * Get a block via a block ID
     * @param block The block ID of the requested block
     * @return The block details, wrapped in a Single
     */
    Single<Block> getBlock(BurstID block);

    /**
     * Get the block at a specific height
     * @param height The height of the block
     * @return The block details, wrapped in a Single
     */
    Single<Block> getBlock(long height);

    /**
     * Get the block at the specified timestamp
     * @param timestamp The timestamp of the block
     * @return The block details, wrapped in a Single
     */
    Single<Block> getBlock(BurstTimestamp timestamp);

    /**
     * Get the block ID at a specified height
     * @param height The height of the block
     * @return The Block ID response, wrapped in a single
     */
    Single<BurstID> getBlockId(long height);

    /**
     * Gets all the blocks between the first index and last index.
     * @param firstIndex The index from the most recent blocks (0 would be the most recent block)
     * @param lastIndex The end index from the most recent blocks
     * @return The blocks, wrapped in a single
     */
    Single<Block[]> getBlocks(long firstIndex, long lastIndex); // TODO includeTransactions?

    /**
     * Get the Constants in use by the node
     * @return The constants, wrapped in a single
     */
    Single<Constants> getConstants();

    /**
     * Get the account details of the specified account
     * @param accountId The address of the account
     * @return The block details, wrapped in a single
     */
    Single<Account> getAccount(BurstAddress accountId);

    /**
     * Get the ATs created by the account
     * @param accountId The address of the account
     * @return A list of the ATs, wrapped in a single
     */
    Single<AT[]> getAccountATs(BurstAddress accountId);

    /**
     * Get the IDs of the blocks forged by an account
     * @param accountId The address of the account
     * @return The block IDs, wrapped in a single
     */
    Single<BurstID[]> getAccountBlockIDs(BurstAddress accountId); // TODO timestamp, firstIndex, lastIndex

    /**
     * Get the blocks forged by an account
     * @param accountId The address of the account
     * @return The blocks, wrapped in a single
     */
    Single<Block[]> getAccountBlocks(BurstAddress accountId); // TODO timestamp, firstIndex, lastIndex, includeTransactions

    /**
     * Get the transaction IDs of an account
     * @param accountId The address of the account
     * @return The account's transaction IDs, wrapped in a single
     */
    Single<BurstID[]> getAccountTransactionIDs(BurstAddress accountId); // TODO filtering

    /**
     * Get the transactions of an account
     * @param accountId The address of the account
     * @return The account's transactions, wrapped in a single
     */
    Single<Transaction[]> getAccountTransactions(BurstAddress accountId); // TODO filtering

    /**
     * Get the list of accounts which have their reward recipient set to the specified account
     * @param accountId The address of the account
     * @return The list of account IDs with reward recipients set to the account, wrapped in a single
     */
    Single<BurstAddress[]> getAccountsWithRewardRecipient(BurstAddress accountId); // TODO finish

    /**
     * Get the details of an AT
     * @param atId The ID of the AT
     * @return The details of the AT, wrapped in a single
     */
    Single<AT> getAt(BurstID atId);

    /**
     * Get the list of IDs of all ATs
     * @return The list of AT IDs, wrapped in a single
     */
    Single<BurstID[]> getAtIds();

    /**
     * Get the details of a transaction
     * @param transactionId The ID of the transaction
     * @return The transaction details, wrapped in a single
     */
    Single<Transaction> getTransaction(BurstID transactionId);

    /**
     * Get the details of a transaction
     * @param fullHash The full hash of the transaction
     * @return The transaction details, wrapped in a single
     */
    Single<Transaction> getTransaction(byte[] fullHash);

    /**
     * Get the transaction bytes
     * @param transactionId The ID of the transaction
     * @return The transaction bytes, wrapped in a single
     */
    Single<byte[]> getTransactionBytes(BurstID transactionId);

    /**
     * Generate a simple transaction (only sending money)
     * @param recipient The recipient
     * @param senderPublicKey The public key of the sender
     * @param amount The amount to send
     * @param fee The transaction fee
     * @param deadline The deadline for the transaction
     * @return The unsigned transaction bytes, wrapped in a single
     */
    Single<byte[]> generateTransaction(BurstAddress recipient, byte[] senderPublicKey, BurstValue amount, BurstValue fee, int deadline);

    /**
     * Generate a transaction with a plaintext message
     * @param recipient The recipient
     * @param senderPublicKey The public key of the sender
     * @param amount The amount to send
     * @param fee The transaction fee
     * @param deadline The deadline for the transaction
     * @param message The message to include in the transaction
     * @return The unsigned transaction bytes, wrapped in a single
     */
    Single<byte[]> generateTransactionWithMessage(BurstAddress recipient, byte[] senderPublicKey, BurstValue amount, BurstValue fee, int deadline, String message);

    /**
     * Generate a transaction with a plaintext message
     * @param recipient The recipient
     * @param senderPublicKey The public key of the sender
     * @param amount The amount to send
     * @param fee The transaction fee
     * @param deadline The deadline for the transaction
     * @param message The message to include in the transaction
     * @return The unsigned transaction bytes, wrapped in a single
     */
    Single<byte[]> generateTransactionWithMessage(BurstAddress recipient, byte[] senderPublicKey, BurstValue amount, BurstValue fee, int deadline, byte[] message);

    /**
     * Generate a transaction with an encrypted message (can be read by sender and recipient)
     * @param recipient The recipient
     * @param senderPublicKey The public key of the sender
     * @param amount The amount to send
     * @param fee The transaction fee
     * @param deadline The deadline for the transaction
     * @param message The encrypted message to include in the transaction
     * @return The unsigned transaction bytes, wrapped in a single
     */
    Single<byte[]> generateTransactionWithEncryptedMessage(BurstAddress recipient, byte[] senderPublicKey, BurstValue amount, BurstValue fee, int deadline, BurstEncryptedMessage message);

    /**
     * Generate a transaction with an encrypted-to-self message (can be read by only sender)
     * @param recipient The recipient
     * @param senderPublicKey The public key of the sender
     * @param amount The amount to send
     * @param fee The transaction fee
     * @param deadline The deadline for the transaction
     * @param message The encrypted message to include in the transaction (Use sender public key and sender private key to encrypt)
     * @return The unsigned transaction bytes, wrapped in a single
     */
    Single<byte[]> generateTransactionWithEncryptedMessageToSelf(BurstAddress recipient, byte[] senderPublicKey, BurstValue amount, BurstValue fee, int deadline, BurstEncryptedMessage message);

    /**
     * Get the currently suggested transaction fees, which are calculated based on current network congestion -
     * @return Suggested transaction fees - Priority, standard and cheap in descending speed and cost, wrapped in a single
     */
    Single<FeeSuggestion> suggestFee();

    /**
     * Get the current mining info
     * @return An observable that returns the current mining info when it changes.
     */
    Observable<MiningInfo> getMiningInfo();

    /**
     * Broadcast a transaction on the network
     * @param transactionBytes The signed transaction bytes
     * @return The number of peers this transaction was broadcast to, wrapped in a single
     */
    Single<Integer> broadcastTransaction(byte[] transactionBytes);

    /**
     * Get the reward recipient of the account
     * @param account The account
     * @return The reward recipient, wrapped in a single
     */
    Single<BurstAddress> getRewardRecipient(BurstAddress account);

    /**
     * Submit a nonce for mining
     * @param passphrase The passphrase of the miner (if solo mining) or null if pool mining
     * @param nonce The nonce that results in the deadline you want to submit
     * @param accountId The account ID of the miner
     * @return The calculated deadline, wrapped in a single
     */
    Single<Long> submitNonce(String passphrase, String nonce, BurstID accountId);

    /**
     * Generate a multi-out transaction
     * @param senderPublicKey The public key of the sender
     * @param fee The transaction fee
     * @param deadline The deadline for the transaction
     * @param recipients A map of recipients and how much they get. Length must be 2-64 inclusive
     * @return The unsigned transaction bytes, wrapped in a single
     * @throws IllegalArgumentException If the number of recipients is not in the range of 2-64 inclusive
     */
    Single<byte[]> generateMultiOutTransaction(byte[] senderPublicKey, BurstValue fee, int deadline, Map<BurstAddress, BurstValue> recipients) throws IllegalArgumentException;

    /**
     * Generate a multi-out transaction
     * @param senderPublicKey The public key of the sender
     * @param amount The amount each recipient gets
     * @param fee The transaction fee
     * @param deadline The deadline for the transaction
     * @param recipients A list of recipients. Each will get the amount specified. Length must be 2-128 inclusive
     * @return The unsigned transaction bytes, wrapped in a single
     * @throws IllegalArgumentException If the number of recipients is not in the range of 2-128 inclusive
     */
    Single<byte[]> generateMultiOutSameTransaction(byte[] senderPublicKey, BurstValue amount, BurstValue fee, int deadline, Set<BurstAddress> recipients) throws IllegalArgumentException;

    /**
     * TODO javadoc
     * @param senderPublicKey
     * @param fee
     * @param deadline
     * @param name
     * @param description
     * @param creationBytes
     * @param code
     * @param dpages
     * @param cspages
     * @param uspages
     * @param minActivationAmount
     * @return
     */
    Single<byte[]> generateCreateATTransaction(byte[] senderPublicKey, BurstValue fee, int deadline, String name, String description, byte[] creationBytes, byte[] code, byte[] data, int dpages, int cspages, int uspages, BurstValue minActivationAmount);

    static BurstNodeService getInstance(String nodeAddress, String userAgent, SchedulerAssigner schedulerAssigner) {
        return new BurstNodeServiceImpl(nodeAddress, userAgent, schedulerAssigner);
    }

    static BurstNodeService getInstance(String nodeAddress, String userAgent) {
        return new BurstNodeServiceImpl(nodeAddress, userAgent, new DefaultSchedulerAssigner());
    }

    static BurstNodeService getInstance(String nodeAddress, SchedulerAssigner schedulerAssigner) {
        return new BurstNodeServiceImpl(nodeAddress, null, schedulerAssigner);
    }

    static BurstNodeService getInstance(String nodeAddress) {
        return new BurstNodeServiceImpl(nodeAddress, null, new DefaultSchedulerAssigner());
    }
}

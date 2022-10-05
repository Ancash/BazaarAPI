package de.ancash.bazaar.core.dao;

import java.util.UUID;

import de.ancash.bazaar.core.Enquiry.EnquiryType;

public interface ITransactionDAO {

	/**
	 * Creates a BuyOrder and returns true on success
	 * 
	 * @param owner
	 * @param amount
	 * @param price
	 * @param cat    - the category
	 * @param sub    - the sub category
	 * @param subsub - the sub sub category
	 * @return success
	 */
	public boolean createBuyOrder(UUID owner, int amount, double price, int cat, int sub, int subsub);

	/**
	 * Creates a SellOffer and returns true on success
	 * 
	 * @param uuid   - players uuid
	 * @param amount - amount
	 * @param price  - unit price
	 * @param cat    - category
	 * @param sub    - sub category
	 * @param subsub - sub sub category
	 * @return success
	 */
	public boolean createSellOffer(UUID uuid, int amount, double price, int cat, int sub, int subsub);

	/**
	 * Checks if the specified player can create an Enquiry
	 * 
	 * @param uuid - the players uuid
	 * @return true if the player can
	 */
	public boolean canCreateEnquiry(UUID uuid);

	public Integer[] sellInstantly(UUID uuid, int amount, double price, int cat, int sub, int subsub);

	public Integer[] buyInstantly(UUID uuid, int amount, double price, int cat, int sub, int subsub);

	public Integer[] cancelEnquiry(UUID uuid, long id, EnquiryType type, double price, int cat, int sub, int subsub);

	public Integer[] collectEnquiry(UUID uuid, long id, EnquiryType type, int max);

	public double collectRemnants(UUID uuid, long id);
}

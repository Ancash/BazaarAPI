package de.ancash.bazaar.core.dao;

import java.util.HashMap;
import java.util.UUID;

import de.ancash.bazaar.core.dao.ICoreDAO;
import de.ancash.bazaar.core.Enquiry.EnquiryType;
import de.ancash.datastructures.tuples.Triplet;

public interface ICoreDAO {

	/**
	 * Returns the {@link ITransactionDAO} used for transactions (e.g creating
	 * BuyOrders/SellOffers, instant transactions and claiming Enquiries)
	 * 
	 * @return
	 */
	public ITransactionDAO transactionDAO();

	/**
	 * Returns the {@link IStatisticsDAO} used for statistics
	 * 
	 * @return
	 */
	public IStatisticsDAO statisticsDAO();

	public IPlaceholderDAO placeholderDAO();

	/**
	 * Returns the highest Enquiry represented as a {@link HashMap}. If there are
	 * multiple Enquiries with the same unit price, the oldest is returned
	 * 
	 * @param type   - type
	 * @param cat    - category
	 * @param sub    - sub category
	 * @param subsub - sub sub category
	 * @return highest Enquiry as map
	 */
	public HashMap<String, Number> getHighestEnquiry(EnquiryType type, int cat, int sub, int subsub);

	/**
	 * Returns the lowest Enquiry represented as a {@link HashMap}.If there are
	 * multiple Enquiries with the same unit price, the oldest is returned
	 * 
	 * @param type   - type
	 * @param cat    - category
	 * @param sub    - sub category
	 * @param subsub - sub sub category
	 * @return lowest Enquiry as map
	 */
	public HashMap<String, Number> getLowestEnquiry(EnquiryType type, int cat, int sub, int subsub);

	/**
	 * Returns the highest Enquiries represented as a nested {@link HashMap}. The
	 * key is the id of the Enquiry
	 * 
	 * @param type   - type
	 * @param cat    - category
	 * @param sub    - sub category
	 * @param subsub - sub sub category
	 * @return highest Enquiries as map
	 */
	public HashMap<Long, HashMap<String, Number>> getHighestEnquiries(EnquiryType type, int cat, int sub, int subsub);

	/**
	 * Returns the lowest Enquiries represented as a nested {@link HashMap}. The key
	 * is the id of the Enquiry
	 * 
	 * @param type   - type
	 * @param cat    - category
	 * @param sub    - sub category
	 * @param subsub - sub sub category
	 * @return lowest Enquiries as map
	 */
	public HashMap<Long, HashMap<String, Number>> getLowestEnquiries(EnquiryType type, int cat, int sub, int subsub);

	/**
	 * Return the highest Enquiry unit price or 0 if there are none.
	 * 
	 * @param type   - type
	 * @param cat    - category
	 * @param sub    - sub category
	 * @param subsub - sub sub category
	 * @return highest Enquiry unit price or 0
	 */
	public double getHighestEnquiryPrice(EnquiryType type, int cat, int sub, int subsub);

	/**
	 * Return the highest Enquiry unit price or the default price if there are none.
	 * 
	 * @param type   - type
	 * @param cat    - category
	 * @param sub    - sub category
	 * @param subsub - sub sub category
	 * @return highest Enquiry unit price or default price
	 */
	public double getHighestEnquiryPriceOrDefault(EnquiryType type, int cat, int sub, int subsub);

	/**
	 * Return the lowest. Enquiry unit price or 0 if there are none.
	 * 
	 * @param type   - type
	 * @param cat    - category
	 * @param sub    - sub category
	 * @param subsub - sub sub category
	 * @return lowest Enquiry unit price or 0
	 */
	public double getLowestEnquiryPrice(EnquiryType type, int cat, int sub, int subsub);

	/**
	 * Return the lowest Enquiry unit price or the default price if there are none.
	 * 
	 * @param type   - type
	 * @param cat    - category
	 * @param sub    - sub category
	 * @param subsub - sub sub category
	 * @return lowest Enquiry unit price or default price
	 */
	public double getLowestEnquiryPriceOrDefault(EnquiryType type, int cat, int sub, int subsub);

	public Triplet<Double, Integer, Integer> getKthLargestEnquiry(EnquiryType type, int cat, int sub, int subsub,
			int k);

	public Triplet<Double, Integer, Integer> getKthSmallestEnquiry(EnquiryType type, int cat, int sub, int subsub,
			int k);

	/**
	 * Returns a specific Enquiry as a map
	 * 
	 * @param uuid - players uuid
	 * @param id   - id
	 * @param type - type
	 * @return map
	 */
	public HashMap<String, Number> getEnquiryAsMap(UUID uuid, long id, EnquiryType type);

	/**
	 * Returns all Enquiries with the same type from the player
	 * 
	 * @param uuid - players uuid
	 * @param type - type
	 * @return map
	 */
	public HashMap<Long, HashMap<String, Number>> getEnquiriesAsMap(UUID uuid, EnquiryType type);

	/**
	 * Returns the sum of the content of all Enquiries in the specified category. If
	 * subsub <= 0, the sum of all Enquiries in the sub category will be returned
	 * 
	 * @param type
	 * @param cat
	 * @param sub
	 * @param subsub
	 * @return sum
	 */
	public int sumEnquiries(EnquiryType type, int cat, int sub, int subsub);

	/**
	 * Returns the sum of all Enquiries in the specified category. If subsub <= 0,
	 * the count of all Enquiries in the sub category will be returned
	 * 
	 * @param type
	 * @param cat
	 * @param sub
	 * @param subsub
	 * @return sum
	 */
	public int countEnquiries(EnquiryType type, int cat, int sub, int subsub);

	/**
	 * Returns the sum of the content of all Enquiries in the specified category. If
	 * subsub <= 0, the sum of all Enquiries in the sub category will be returned
	 * 
	 * @param type
	 * @param cat
	 * @param sub
	 * @param subsub
	 * @return sum
	 */
	public int lazySumEnquiries(EnquiryType type, int cat, int sub, int subsub);

	/**
	 * Returns the sum of all Enquiries in the specified category. If subsub <= 0,
	 * the count of all Enquiries in the sub category will be returned
	 * 
	 * @param type
	 * @param cat
	 * @param sub
	 * @param subsub
	 * @return count
	 */
	public int lazyCountEnquiries(EnquiryType type, int cat, int sub, int subsub);

	/**
	 * Returns the numbe of Enquiries owned by player
	 * 
	 * @param uuid - players uuid
	 * @return count
	 */
	public int countEnquiries(UUID uuid);

	/**
	 * Returns true if Enquiries exist in the specified category
	 * 
	 * @param type   - type
	 * @param cat    - category
	 * @param sub    - sub category
	 * @param subsub - sub sub category
	 * @return exist enquiries
	 */
	public boolean existEnquiries(EnquiryType type, int cat, int sub, int subsub);

	/**
	 * Returns the players claimable coins
	 * 
	 * @param uuid - players uuid
	 * @return coins
	 */
	public double getClaimableCoins(UUID uuid);

	/**
	 * Returns the numbere of claimable items
	 * 
	 * @param uuid - players uuid
	 * @return claimable
	 */
	public int getClaimableItems(UUID uuid);

	/**
	 * See {@link ICoreDAO#getClaimableCoins(UUID)} and
	 * {@link ICoreDAO#getClaimableItems(UUID)}
	 * 
	 * @param uuid
	 * @param id
	 * @param type
	 * @return claimable
	 */
	public int getClaimable(UUID uuid, long id, EnquiryType type);

	/**
	 * Returns how many are left
	 * 
	 * @param uuid - players uuid
	 * @param id   - id
	 * @param type - type
	 * @return left
	 */
	public int getLeft(UUID uuid, long id, EnquiryType type);

	/**
	 * Returns tax in %
	 * 
	 * @return tax
	 */
	public int getTax();

	/**
	 * Returns all remnants from the player. See
	 * {@link ICoreDAO#getRemnants(UUID, long)}
	 * 
	 * @param id
	 * @return remnants
	 */
	public double getRemnants(UUID id);

	/**
	 * Returns the remnants of the specific BuyOrder. Remnants are created when a
	 * BuyOrder and a SellOffer do not have the same unit price but fill each other.
	 * Only BuyOrders can have remnants.
	 * 
	 * @param uuid
	 * @param id
	 * @return remnants
	 */
	public double getRemnants(UUID uuid, long id);
}

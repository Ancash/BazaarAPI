package de.ancash.bazaar.core.dao;

import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

import de.ancash.bazaar.core.Enquiry.EnquiryType;
import de.ancash.datastructures.tuples.Duplet;
import de.ancash.datastructures.tuples.Triplet;

public interface IPlaceholderDAO {

	public Triplet<HashMap<String, String>, HashMap<String, String>, HashMap<String, String>> getCreateBuyOrderPlaceholder(
			int amount, int cat, int sub, int subsub);

	public HashMap<String, String> getPlaceholders(int cat, int sub, int subsub, Optional<Integer> topSellOffers,
			Optional<Integer> topBuyOrders);

	public HashMap<String, String> getSubSubPlaceholders(UUID player, int cat, int sub, int subsub, int invContent,
			Optional<Integer> topOrders, Optional<Integer> topOffers);

	public HashMap<String, String> getBuyInstantlyPlaceholder(int cat, int sub, int subsub);

	public HashMap<String, String> getManageEnquiriesPlaceholderMain(UUID id);

	public HashMap<String, String> getCancelEnquiriesPlaceholder(UUID player, long id, EnquiryType type);

	public Duplet<HashMap<String, HashMap<String, Number>>, HashMap<String, HashMap<String, Number>>> getManageEnquiriesPlaceholder(
			UUID id);
}

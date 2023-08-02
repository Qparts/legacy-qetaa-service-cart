package qetaa.service.cart.restful;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import qetaa.service.cart.dao.DAO;
import qetaa.service.cart.filters.SecuredUser;
import qetaa.service.cart.helpers.AppConstants;
import qetaa.service.cart.helpers.Helper;
import qetaa.service.cart.model.reports.MakeOrderMonth;
import qetaa.service.cart.model.reports.MonthOrders;

@Path("report/")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CartReportService {

	@EJB
	private DAO dao;

	@SecuredUser
	@GET
	@Path("carts-count/date/{param}")
	public Response getCartsCount(@PathParam(value = "param") long dateLong) {
		try {
			Date from = new Date(dateLong);
			Calendar cFrom = Calendar.getInstance();
			cFrom.setTime(from);
			cFrom.set(Calendar.HOUR_OF_DAY, 0);
			cFrom.set(Calendar.MINUTE, 0);
			cFrom.set(Calendar.SECOND, 0);
			cFrom.set(Calendar.MILLISECOND, 0);
			from.setTime(cFrom.getTimeInMillis());

			Date to = new Date(dateLong);
			Calendar cTo = Calendar.getInstance();
			cTo.setTime(to);
			cTo.set(Calendar.HOUR_OF_DAY, 23);
			cTo.set(Calendar.MINUTE, 59);
			cTo.set(Calendar.SECOND, 59);
			cTo.set(Calendar.MILLISECOND, 999);
			to.setTime(cTo.getTimeInMillis());

			String jpql = "select count(b) from Cart b where b.created between :value0 and :value1";
			Long count = dao.findJPQLParams(Long.class, jpql, from, to);
			return Response.status(200).entity(count).build();
		} catch (Exception ex) {
			return Response.status(500).build();
		}
	}

	@SecuredUser
	@GET
	@Path("parts-orders-count/date/{param}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getPartsOrdersCount(@PathParam(value = "param") long dateLong) {
		try {
			Date from = new Date(dateLong);
			Calendar cFrom = Calendar.getInstance();
			cFrom.setTime(from);
			cFrom.set(Calendar.HOUR_OF_DAY, 0);
			cFrom.set(Calendar.MINUTE, 0);
			cFrom.set(Calendar.SECOND, 0);
			cFrom.set(Calendar.MILLISECOND, 0);
			from.setTime(cFrom.getTimeInMillis());

			Date to = new Date(dateLong);
			Calendar cTo = Calendar.getInstance();
			cTo.setTime(to);
			cTo.set(Calendar.HOUR_OF_DAY, 23);
			cTo.set(Calendar.MINUTE, 59);
			cTo.set(Calendar.SECOND, 59);
			cTo.set(Calendar.MILLISECOND, 999);
			to.setTime(cTo.getTimeInMillis());

			String jpql = "select count(b) from PartsOrder b where b.created between :value0 and :value1";
			Long count = dao.findJPQLParams(Long.class, jpql, from, to);
			return Response.status(200).entity(count).build();
		} catch (Exception ex) {
			return Response.status(500).build();
		}
	}

	@SecuredUser
	@GET
	@Path("carts-count/archived/date/{param}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getCartsCountArchived(@PathParam(value = "param") long dateLong) {
		try {
			Date from = new Date(dateLong);
			Calendar cFrom = Calendar.getInstance();
			cFrom.setTime(from);
			cFrom.set(Calendar.HOUR_OF_DAY, 0);
			cFrom.set(Calendar.MINUTE, 0);
			cFrom.set(Calendar.SECOND, 0);
			cFrom.set(Calendar.MILLISECOND, 0);
			from.setTime(cFrom.getTimeInMillis());

			Date to = new Date(dateLong);
			Calendar cTo = Calendar.getInstance();
			cTo.setTime(to);
			cTo.set(Calendar.HOUR_OF_DAY, 23);
			cTo.set(Calendar.MINUTE, 59);
			cTo.set(Calendar.SECOND, 59);
			cTo.set(Calendar.MILLISECOND, 999);
			to.setTime(cTo.getTimeInMillis());
			String jpql = "select count(b) from Cart b where b.id in ("
					+ "select c.cartId from CartReview c where c.created between :value0 and :value1 "
					+ "and c.status = :value2 and c.actionValue = :value3" + ")";
			Long count = dao.findJPQLParams(Long.class, jpql, from, to, 'C', 'X');
			return Response.status(200).entity(count).build();
		} catch (Exception ex) {
			return Response.status(500).build();
		}
	}

	@SecuredUser
	@GET
	@Path("submitted-quotation-count/date/{param}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getQuotationSubmittedCount(@PathParam(value = "param") long dateLong) {
		try {
			Date from = new Date(dateLong);
			Calendar cFrom = Calendar.getInstance();
			cFrom.setTime(from);
			cFrom.set(Calendar.HOUR_OF_DAY, 0);
			cFrom.set(Calendar.MINUTE, 0);
			cFrom.set(Calendar.SECOND, 0);
			cFrom.set(Calendar.MILLISECOND, 0);
			from.setTime(cFrom.getTimeInMillis());

			Date to = new Date(dateLong);
			Calendar cTo = Calendar.getInstance();
			cTo.setTime(to);
			cTo.set(Calendar.HOUR_OF_DAY, 23);
			cTo.set(Calendar.MINUTE, 59);
			cTo.set(Calendar.SECOND, 59);
			cTo.set(Calendar.MILLISECOND, 999);
			to.setTime(cTo.getTimeInMillis());

			String jpql = "select count(b) from Cart b where b.submitted between :value0 and :value1";
			Long count = dao.findJPQLParams(Long.class, jpql, from, to);
			return Response.status(200).entity(count).build();
		} catch (Exception ex) {
			return Response.status(500).build();
		}
	}

	@SecuredUser
	@GET
	@Path("wire-transfer-count/date/{param}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getWireTransferCount(@PathParam(value = "param") long dateLong) {
		try {
			Date from = new Date(dateLong);
			Calendar cFrom = Calendar.getInstance();
			cFrom.setTime(from);
			cFrom.set(Calendar.HOUR_OF_DAY, 0);
			cFrom.set(Calendar.MINUTE, 0);
			cFrom.set(Calendar.SECOND, 0);
			cFrom.set(Calendar.MILLISECOND, 0);
			from.setTime(cFrom.getTimeInMillis());

			Date to = new Date(dateLong);
			Calendar cTo = Calendar.getInstance();
			cTo.setTime(to);
			cTo.set(Calendar.HOUR_OF_DAY, 23);
			cTo.set(Calendar.MINUTE, 59);
			cTo.set(Calendar.SECOND, 59);
			cTo.set(Calendar.MILLISECOND, 999);
			to.setTime(cTo.getTimeInMillis());
			String jpql = "select count(b) from WireTransfer b where b.created between :value0 and :value1";
			Long count = dao.findJPQLParams(Long.class, jpql, from, to);
			return Response.status(200).entity(count).build();
		} catch (Exception ex) {
			return Response.status(500).build();
		}
	}

	@SecuredUser
	@GET
	@Path("follow-up-review-count/date/{param}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getFollowUpReviewCount(@PathParam(value = "param") long dateLong) {
		try {
			Date from = new Date(dateLong);
			Calendar cFrom = Calendar.getInstance();
			cFrom.setTime(from);
			cFrom.set(Calendar.HOUR_OF_DAY, 0);
			cFrom.set(Calendar.MINUTE, 0);
			cFrom.set(Calendar.SECOND, 0);
			cFrom.set(Calendar.MILLISECOND, 0);
			from.setTime(cFrom.getTimeInMillis());

			Date to = new Date(dateLong);
			Calendar cTo = Calendar.getInstance();
			cTo.setTime(to);
			cTo.set(Calendar.HOUR_OF_DAY, 23);
			cTo.set(Calendar.MINUTE, 59);
			cTo.set(Calendar.SECOND, 59);
			cTo.set(Calendar.MILLISECOND, 999);
			to.setTime(cTo.getTimeInMillis());
			String jpql = "select count(b) from CartReview b where b.created between :value0 and :value1 and b.stage = :value2";
			Long count = dao.findJPQLParams(Long.class, jpql, from, to, 3);
			return Response.status(200).entity(count).build();
		} catch (Exception ex) {
			return Response.status(500).build();
		}
	}

	@SecuredUser
	@GET
	@Path("order-makes/last-year")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getMakeOrdersReportLastYear(@HeaderParam("Authorization") String authHeader) {
		try {
			List<Calendar> calendars = getPast12Months();
			Date to = new Date();
			Date from = new Date(to.getTime() - (365L * 24 * 60 * 60 * 1000));
			String jpql = "select distinct c.makeId from Cart c where c.created between :value0 and :value1";
			List<Integer> makeIds = dao.getJPQLParams(Integer.class, jpql, from, to);
			List<MakeOrderMonth> moms = new ArrayList<>();
			for (Integer makeId : makeIds) {
				MakeOrderMonth mom = new MakeOrderMonth();
				mom.setMonthOrders(new ArrayList<>());
				mom.setMake(this.getMakeFromId(makeId, authHeader));
				mom.setMakeId(makeId.intValue());

				for (Calendar c : calendars) {
					MonthOrders mo = new MonthOrders();
					mo.setMonth(c.get(Calendar.MONTH));
					mo.initMonthName();
					int year = c.get(Calendar.YEAR);
					int month = mo.getMonth() + 1;
					String monthString = Integer.valueOf(month).toString();
					if (month < 10) {
						monthString = "0" + monthString;
					}
					String sql = "select count(*) from crt_cart b where b.make_id = " + makeId
							+ " and to_char(b.created, 'YYYY-MM') = '" + year + "-" + monthString + "'";
					List<Object> counts = dao.getNative(sql);
					BigInteger bi = (BigInteger) counts.get(0);
					mo.setCarts(bi.intValue());

					String sql2 = "select count(*) from crt_parts_order b where b.cart_id in ("
							+ "select c.id from crt_cart c where c.make_id = " + makeId
							+ " and to_char(c.created, 'YYYY-MM') = '" + year + "-" + monthString + "')";
					List<Object> counts2 = dao.getNative(sql2);
					BigInteger bi2 = (BigInteger) counts2.get(0);
					mo.setPartOrders(bi2.intValue());
					mom.getMonthOrders().add(mo);
				}
				moms.add(mom);
			}
			return Response.status(200).entity(moms).build();

		} catch (Exception ex) {
			ex.printStackTrace();
			return Response.status(500).build();
		}

	}

	@SecuredUser
	@GET
	@Path("cart-cities/from/{from}/to/{to}/make/{makeId}/archived/{archived}/ordered/{ordered}")
	public Response getCityCartsFiltered(@PathParam(value = "from") long from, @PathParam(value = "to") long to,
			@PathParam(value = "makeId") int makeId, @PathParam(value = "archived") boolean archived,
			@PathParam(value = "ordered") boolean ordered) {
		try {
			List<Map<String, Number>> list = new ArrayList<>();
			String sql = "select c.city_id, count(c.*) as count from crt_cart c where c.id > 0 and c.city_id is not null ";
			if (ordered) {
				sql += " and c.id in ( select p.cart_id from crt_parts_order p where p.cart_id = c.id)";
			}
			if (!archived) {
				sql += " and c.status != 'X'";
			}
			if(makeId != 0) {
				sql += " and c.make_id = " + makeId;
			}
			Helper h = new Helper();
			String fromString = h.getDateFormat(new Date(from), "yyyy-MM-dd");
			String toString = h.getDateFormat(new Date(to), "yyyy-MM-dd");

			sql += " and c.created\\:\\:date between '" + fromString + "' and '" + toString + "'";
			sql +=" group by city_id";

			List<Object> objects = dao.getNative(sql);
			for (Object obj : objects) {
				Map<String, Number> map = new HashMap<String, Number>();
				Object[] values = (Object[]) obj;
				Number cityId = (Number) values[0];
				Number count = (Number) values[1];
				map.put("cityId", cityId);
				map.put("count", count);
				list.add(map);
			}

			return Response.status(200).entity(list).build();
		} catch (Exception ex) {
			return Response.status(500).build();
		}
	}

	@GET
	@Path("cart-cities")
	public Response getCityCarts() {
		try {
			List<Map<String, Number>> list = new ArrayList<>();
			String sql = "select city_id, count(*) from crt_cart group by city_id order by city_id";
			List<Object> objects = dao.getNative(sql);
			for (Object obj : objects) {
				Map<String, Number> map = new HashMap<String, Number>();
				Object[] values = (Object[]) obj;
				Number cityId = (Number) values[0];
				Number count = (Number) values[1];
				map.put("cityId", cityId);
				map.put("count", count);
				list.add(map);
			}
			return Response.status(200).entity(list).build();
		} catch (Exception ex) {
			return Response.status(500).build();
		}
	}

	@SuppressWarnings("unchecked")
	private Map<String, Object> getMakeFromId(int makeId, String authHeader) throws Exception {
		Response r = this.getSecuredRequest(AppConstants.getMake(makeId), authHeader);
		if (r.getStatus() == 200) {
			Map<String, Object> object = r.readEntity(Map.class);
			return object;
		} else
			throw new Exception();
	}

	private List<Calendar> getPast12Months() {
		Calendar calendar = Calendar.getInstance();
		List<Calendar> allDates = new ArrayList<>();
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		for (int i = 0; i <= 11; i++) {
			Calendar c = Calendar.getInstance();
			c.setTime(calendar.getTime());
			c.add(Calendar.MONTH, -i);
			allDates.add(c);
		}

		Collections.sort(allDates, new Comparator<Calendar>() {
			@Override
			public int compare(Calendar o1, Calendar o2) {
				return o1.getTime().compareTo(o2.getTime());
			}
		});

		return allDates;

	}

	public Response getSecuredRequest(String link, String authHeader) {
		Builder b = ClientBuilder.newClient().target(link).request();
		b.header(HttpHeaders.AUTHORIZATION, authHeader);
		Response r = b.get();
		return r;
	}

}

import java.util.*;
import java.util.stream.Collectors;

import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.message.BasicHeader;
import org.hibernate.Session;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.sberbank.kuzin19190813.app.Application;
import ru.sberbank.kuzin19190813.db.HibernateUtil;
import ru.sberbank.kuzin19190813.db.dao.DAOHolder;
import ru.sberbank.kuzin19190813.db.dao.impl.TokenDAO;
import ru.sberbank.kuzin19190813.exceptions.CardNotFoundException;
import ru.sberbank.kuzin19190813.exceptions.IllegalParameterException;
import ru.sberbank.kuzin19190813.model.Card;
import ru.sberbank.kuzin19190813.model.Token;
import ru.sberbank.kuzin19190813.model.User;
import ru.sberbank.kuzin19190813.service.CardServiceInterface;
import ru.sberbank.kuzin19190813.service.CardService;
import ru.sberbank.kuzin19190813.service.UserService;
import ru.sberbank.kuzin19190813.view.input_pojos.AddCounterpartyPOJO;
import ru.sberbank.kuzin19190813.view.input_pojos.CreateCardPOJO;
import ru.sberbank.kuzin19190813.view.input_pojos.IncrementBalancePOJO;
import ru.sberbank.kuzin19190813.view.input_pojos.PaymentPOJO;
import ru.sberbank.kuzin19190813.view.output_pojos.*;
import ru.sberbank.kuzin19190813.winter_framework.constants.HttpMethod;

public class Tests {
    private static final String testToken = "token12345";

    private static final Set<Header> defaultHeaders = Arrays.stream(new Header[]{
            new BasicHeader(HttpHeaders.CONTENT_TYPE, "application/json"),
            new BasicHeader(HttpHeaders.ACCEPT, "application/json"),
            new BasicHeader("token", testToken)
    }).collect(Collectors.toSet());

    private static final Set<Header> headersWithoutToken = Arrays.stream(new Header[]{
            new BasicHeader(HttpHeaders.CONTENT_TYPE, "application/json"),
            new BasicHeader(HttpHeaders.ACCEPT, "application/json")
    }).collect(Collectors.toSet());

    public static void dropTables() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.createNativeQuery("drop table SBERUSER; drop table CARD; drop table COUNTERPARTIES;")
                    .addEntity(User.class)
                    .addEntity(Card.class)
                    .getResultList();
            session.getTransaction().commit();
        }
    }

    @BeforeEach
    public void startServer() {
        //dropTables();
        Token token = new Token();
        token.setToken(testToken);
        new TokenDAO().save(token);
        Application.start();
    }

    //CARDS API

    @Test
    public void testCreateCard() {
        int cardsQuantity = 9;
        User user = new UserService().createUser();
        for (int i = 1; i <= cardsQuantity; i++) {
            JSONObject body = generateCreateCardJSON(user.getId(), i);
            testControllerPOST("/cards/create-card", body, 200, MessagePOJO.ok().toString());
        }
        List<Card> cardList = DAOHolder.getCardDAO().getAll();
        Assertions.assertEquals(cardsQuantity, cardList.size());
        int i = new Random().nextInt(cardsQuantity);
        String expected = generateCardNumber(i + 1);
        String actual = cardList.get(i).getCardNumber();
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void testGetCardList() {
        testGetCardsList(null, 400, ErrorPOJO.parameterError("userId").toString());
        testGetCardsList("1", 404, ErrorPOJO.notFound("user", "1").toString());
        testGetCardsList("1", 200, "{\"cards\":[]}", Tests::createUser);
        testGetCardsList("1", 200, generateCardsListJSON(1L).toString(), Tests::createUserThenCards);
    }

    @Test
    public void testGetCardListWithoutToken() {
        TestUtils.TestResponse response = TestUtils.executeRequest(
                "http://localhost:8000" + "/cards/list?userId=1",
                HttpMethod.GET,
                headersWithoutToken
        );
        if (response == null) Assertions.fail("no any response was got");
        Assertions.assertEquals(ErrorPOJO.invalidToken().toString(), response.getContent());
        Assertions.assertEquals(403, response.getCode());
    }

    @Test
    public void testUpdateBalance() {
        int testConst = 5;
        testUpdateBalance(404, ErrorPOJO.notFound("card", generateAccountNumber(testConst)).toString(), generateIncrementBalanceJSON(testConst));
        testUpdateBalance(200, MessagePOJO.ok().toJSONObject().toString(), generateIncrementBalanceJSON(testConst), Tests::createUserThenCards);
        testUpdateBalance(400, new ErrorPOJO("updated balance is less then zero").toString(), generateIncrementBalanceJSON(testConst, -10000.0));
        testUpdateBalance(400, new ErrorPOJO("invalid request data: java.lang.NumberFormatException: For input string: \"invalid_data\"").toString(), generateIncrementBalanceJSON(testConst).put("amount", "invalid_data"));
        Card card = DAOHolder.getCardDAO().get((long) testConst);
        if (card == null) Assertions.fail("fail to create cards or card not found");
        Assertions.assertEquals(1000 * testConst, card.getBalance());
    }

    @Test
    public void testGetBalance() throws CardNotFoundException, IllegalParameterException {
        String accountNumber = generateAccountNumber(8);
        testGetBalance(accountNumber, 200, new BalancePOJO(accountNumber, 0.0).toJSONObject().toString(), Tests::createUserThenCards);
        new CardService().updateBalance(accountNumber, 350.0);
        testGetBalance(accountNumber, 200, new BalancePOJO(accountNumber, 350.0).toJSONObject().toString(), Tests::createUserThenCards);
    }

    //COUNTERPARTY API

    @Test
    public void testAddCounterparty() {
        testAddCounterparty(1L, 2L, 400, ErrorPOJO.notFound("user or counterparty",
                String.format(" %s and %s ", 1L, 2L)).toString());
        createUsers(10);
        testAddCounterparty(1L, 2L, 200, MessagePOJO.ok().toString());
        testAddCounterparty(1L, 3L, 200, MessagePOJO.ok().toString());
        Assertions.assertEquals(2, DAOHolder.getUserDAO().getWithCounterparties(1L).getCounterparties().size());
        testAddCounterparty(1L, 2L, 200, new MessagePOJO("counterparty already exists").toString());
        Assertions.assertEquals(2, DAOHolder.getUserDAO().getWithCounterparties(1L).getCounterparties().size());

    }

    @Test
    public void testGetCounterparties() {
        testGetCounterparties(1L, 404, ErrorPOJO.notFound("user", 1).toString());
        createUsers(10);
        testGetCounterparties(1L, 200, new CounterpartiesListPOJO(new ArrayList<CounterpartiesListPOJO.CounterpartyPOJO>()).toJSONObject().toString());
        //add counterparties to user 1
        testAddCounterparty(1L, 2L, 200, MessagePOJO.ok().toString());
        testAddCounterparty(1L, 3L, 200, MessagePOJO.ok().toString());
        testAddCounterparty(1L, 4L, 200, MessagePOJO.ok().toString());
        testAddCounterparty(1L, 5L, 200, MessagePOJO.ok().toString());
        List<CounterpartiesListPOJO.CounterpartyPOJO> counterpartyPOJOS = new ArrayList<>();
        counterpartyPOJOS.add(new CounterpartiesListPOJO.CounterpartyPOJO(2L, ""));
        counterpartyPOJOS.add(new CounterpartiesListPOJO.CounterpartyPOJO(3L, ""));
        counterpartyPOJOS.add(new CounterpartiesListPOJO.CounterpartyPOJO(4L, ""));
        counterpartyPOJOS.add(new CounterpartiesListPOJO.CounterpartyPOJO(5L, ""));

        testGetCounterparties(1L, 200, new CounterpartiesListPOJO(counterpartyPOJOS).toJSONObject().toString());
    }

    @Test
    public void testSendPayments() {
        testSendPayment(1L, 2L, "123", "321", 105.0, 404, new ErrorPOJO("user not found").toString());
        createUsers(10);
        createCards(1L, 1, 2);
        createCards(2L, 2, 3);
        testAddCounterparty(1L, 2L, 200, MessagePOJO.ok().toString());
        String fromAccountNumber = String.valueOf(11111111);
        try {
            new CardService().updateBalance(fromAccountNumber, 200.0);
        } catch (CardNotFoundException | IllegalParameterException e) {
            e.printStackTrace();
        }
        testSendPayment(1L, 2L,  fromAccountNumber, String.valueOf(11111111*2) ,100.0, 200, MessagePOJO.ok().toString());
    }

    //TEST CONTROLLER

    private void testController(String path, HttpMethod httpMethod, JSONObject body, int expectedCode, String expectedContent) {
        TestUtils.TestResponse response = TestUtils.executeRequest(
                "http://localhost:8000" + path,
                httpMethod,
                defaultHeaders,
                body
        );
        if (response == null) Assertions.fail("no any response was got");
        Assertions.assertEquals(expectedContent, response.getContent());
        Assertions.assertEquals(expectedCode, response.getCode());
    }

    private void testControllerGET(String path, int expectedCode, String expectedContent) {
        testController(path, HttpMethod.GET, null, expectedCode, expectedContent);
    }

    private void testControllerPOST(String path, JSONObject body, int expectedCode, String expectedContent) {
        testController(path, HttpMethod.POST, body, expectedCode, expectedContent);
    }

    //TEST GET CARDS LIST

    private void testGetCardsList(String userId, int expectedCode, String expectedContent, TestUtils.BeforeInitiator beforeInitiator) {
        beforeInitiator.doBefore();
        String paramsString = "";
        if (userId != null) paramsString = "?userId=" + userId;
        testControllerGET("/cards/list/"+paramsString, expectedCode, expectedContent);
    }

    private void testGetCardsList(String userId, int expectedCode, String expectedContent) {
        testGetCardsList(userId, expectedCode, expectedContent, ()->{});
    }

    //TEST UPDATE BALANCE

    public void testUpdateBalance(int expectedCode, String expectedContent, JSONObject body, TestUtils.BeforeInitiator beforeInitiator) {
        beforeInitiator.doBefore();
        testControllerPOST("/cards/update-balance/", body, expectedCode, expectedContent);
    }

    private void testUpdateBalance(int expectedCode, String expectedContent, JSONObject body) {
        testUpdateBalance(expectedCode, expectedContent, body, ()->{});
    }

    //TEST GET BALANCE

    public void testGetBalance(String cardNumber, int expectedCode, String expectedContent, TestUtils.BeforeInitiator beforeInitiator) {
        beforeInitiator.doBefore();
        testControllerGET("/cards/balance/?accountNumber="+TestUtils.reformatParameters(cardNumber), expectedCode, expectedContent);
    }

    private void testGetBalance(String accountNumber, int expectedCode, String expectedContent) {
        testGetBalance(accountNumber, expectedCode, expectedContent, ()->{});
    }

    //TEST ADD COUNTERPARTY
    public void testAddCounterparty(Long userId, Long counterpartyId, int expectedCode, String expectedContent) {
            testControllerPOST("/counterparty/add/", new AddCounterpartyPOJO(userId, counterpartyId).toJSONObject(), expectedCode, expectedContent);
    }

    //TEST GET COUNTERPARTIES
    public void testGetCounterparties(Long userId, int expectedCode, String expectedContent) {
        testControllerGET("/counterparty/list?userId="+userId, expectedCode, expectedContent);
    }

    //TEST SEND PAYMENT
    public void testSendPayment(Long userId, Long counterpartyId, String fromAccountNumber, String toAccountNumber, Double amount, int expectedCode, String expectedContent) {
        PaymentPOJO paymentPOJO = new PaymentPOJO();
        paymentPOJO.setUserId(userId);
        paymentPOJO.setCounterpartyId(counterpartyId);
        paymentPOJO.setFromAccountNumber(fromAccountNumber);
        paymentPOJO.setToAccountNumber(toAccountNumber);
        paymentPOJO.setAmount(amount);
        System.out.println("toAccountNumber = " + toAccountNumber);
        testControllerPOST("/counterparty/send-payment", paymentPOJO.toJSONObject(), expectedCode, expectedContent);
    }

    //GENERATORS

    private JSONObject generateCreateCardJSON(Long userId, int i) {
        CreateCardPOJO createCardPOJO = new CreateCardPOJO();
        createCardPOJO.setUserId(userId);
        createCardPOJO.setAccountNumber(generateAccountNumber(i));
        createCardPOJO.setCardNumber(generateCardNumber(i));
        return createCardPOJO.toJSONObject();
    }

    private JSONObject generateCardJSON(Long userId, int i) {
        CardsListPOJO.CardPOJO cardPOJO = new CardsListPOJO.CardPOJO();
        cardPOJO.setUserId(userId);
        cardPOJO.setCardNumber(generateCardNumber(i));
        cardPOJO.setAccountNumber(generateAccountNumber(i));
        cardPOJO.setBalance(0.0);
        cardPOJO.setId((long) i);
        return cardPOJO.toJSONObject();
    }

    private JSONObject generateCardsListJSON(Long userId) {
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        for (int i = 1; i < 10; i++) {
            jsonArray.put(generateCardJSON(userId, i));
        }
        jsonObject.put("cards", jsonArray);
        return jsonObject;
    }

    private JSONObject generateIncrementBalanceJSON(int i, Double amount) {
        IncrementBalancePOJO incrementBalancePOJO = new IncrementBalancePOJO();
        incrementBalancePOJO.setAccountNumber(generateAccountNumber(i));
        incrementBalancePOJO.setAmount(amount);
        return incrementBalancePOJO.toJSONObject();
    }

    private JSONObject generateIncrementBalanceJSON(int i) {
        return generateIncrementBalanceJSON(i, i*1000.0);
    }

    private static String generateCardNumber(int i) {
        int quart = i * 1111;
        return String.format("%s %s %s %s", quart, quart, quart, quart);
    }

    private static String generateAccountNumber(int i) {
        return String.valueOf(i * 11111111);
    }

    private static void createCards(Long userId, int startI, int endI) {
        CardServiceInterface cardServiceInterface = new CardService();
        for (int i = startI; i < endI; i++) {
            cardServiceInterface.createCard(userId, generateAccountNumber(i), generateCardNumber(i));
        }
    }

    private static void createCards() {
        createCards(1L, 1, 10);
    }

    private static void createUser() {
        new UserService().createUser();
    }

    private static void createUsers(int count) {
        for (int i = 0; i < count; i++) {
            new UserService().createUser();
        }
    }

    private static void createUserThenCards() {
        createUser();
        createCards();
    }

}

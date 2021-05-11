package uk.gov.gchq.gaffer.federatedstore;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

import uk.gov.gchq.gaffer.accumulostore.AccumuloProperties;
import uk.gov.gchq.gaffer.accumulostore.AccumuloStore;
import uk.gov.gchq.gaffer.accumulostore.SingleUseMockAccumuloStore;
import uk.gov.gchq.gaffer.commonutil.iterable.CloseableIterable;
import uk.gov.gchq.gaffer.data.element.Edge;
import uk.gov.gchq.gaffer.data.element.Element;
import uk.gov.gchq.gaffer.data.elementdefinition.view.View;
import uk.gov.gchq.gaffer.jsonserialisation.JSONSerialiser;
import uk.gov.gchq.gaffer.mapstore.MapStore;
import uk.gov.gchq.gaffer.mapstore.MapStoreProperties;
import uk.gov.gchq.gaffer.operation.OperationException;
import uk.gov.gchq.gaffer.operation.graph.SeededGraphFilters;
import uk.gov.gchq.gaffer.operation.impl.add.AddElements;
import uk.gov.gchq.gaffer.operation.impl.get.GetAllElements;
import uk.gov.gchq.gaffer.operation.impl.get.GetElements;
import uk.gov.gchq.gaffer.store.Context;
import uk.gov.gchq.gaffer.store.Store;
import uk.gov.gchq.gaffer.store.StoreException;
import uk.gov.gchq.gaffer.store.schema.Schema;
import uk.gov.gchq.gaffer.store.schema.SchemaEdgeDefinition;
import uk.gov.gchq.gaffer.types.TypeSubTypeValue;
import uk.gov.gchq.gaffer.user.User;

import java.util.ArrayList;

public class BugHuntTest {

    public static final String TSTV = TypeSubTypeValue.class.getSimpleName();
    public static final String A = "a";
    public static final String AA = "aa";
    public static final String AAA = "aaa";
    public static final String B = "b";
    public static final String BB = "bb";
    public static final String BBB = "bbb";
    public static final Context CONTEXT = new Context(new User("user"));
    public static final String TEST_EDGE = "testEdge";
    public static final String EXPECTED = "{\n" +
            "  \"class\" : \"uk.gov.gchq.gaffer.data.element.Edge\",\n" +
            "  \"group\" : \"testEdge\",\n" +
            "  \"source\" : {\n" +
            "    \"uk.gov.gchq.gaffer.types.TypeSubTypeValue\" : {\n" +
            "      \"type\" : \"a\",\n" +
            "      \"subType\" : \"aa\",\n" +
            "      \"value\" : \"aaa\"\n" +
            "    }\n" +
            "  },\n" +
            "  \"destination\" : {\n" +
            "    \"uk.gov.gchq.gaffer.types.TypeSubTypeValue\" : {\n" +
            "      \"type\" : \"b\",\n" +
            "      \"subType\" : \"bb\",\n" +
            "      \"value\" : \"bbb\"\n" +
            "    }\n" +
            "  },\n" +
            "  \"directed\" : false,\n" +
            "  \"matchedVertex\" : \"SOURCE\",\n" +
            "  \"properties\" : { }\n" +
            "}";

    @Test
    public void testGetAllElementsFromMap() throws Exception {
        MapStore store = getMapStore();

        addElement(store);

        CloseableIterable<? extends Element> results = store.execute(new GetAllElements.Builder()
                .view(new View.Builder().edge(TEST_EDGE).build())
                .build(), CONTEXT);

        Assert.assertNotNull(results);
        Assert.assertTrue(results.iterator().hasNext());

        ArrayList<Object> list = new ArrayList<>();
        for (Element result : results) {
            list.add(result);
        }

        Assert.assertEquals(1, list.size());
        Assert.assertEquals(EXPECTED, new String(JSONSerialiser.serialise(list.get(0), true)));

    }

    @Test
    public void testGetElementsFromMap() throws Exception {
        MapStore store = getMapStore();

        addElement(store);

        CloseableIterable<? extends Element> results = store.execute(new GetElements.Builder()
                .input(new Edge.Builder()
                        .group(TEST_EDGE)
                        .source(new TypeSubTypeValue(A, AA, AAA))
                        //        .source(A)
                        .dest(new TypeSubTypeValue(B, BB, BBB))
                        //        .dest(B)
                        .build())
                .view(new View.Builder().build())
                .inOutType(SeededGraphFilters.IncludeIncomingOutgoingType.EITHER)
                .build(), CONTEXT);

        Assert.assertNotNull(results);
        Assert.assertTrue(results.iterator().hasNext());

        ArrayList<Object> list = new ArrayList<>();
        for (Element result : results) {
            list.add(result);
        }

        Assert.assertEquals(1, list.size());
        Assert.assertEquals(EXPECTED, new String(JSONSerialiser.serialise(list.get(0))));
    }

    @Test
    public void testGetElementsNoViewFromMap() throws Exception {
        MapStore store = getMapStore();

        addElement(store);

        CloseableIterable<? extends Element> results = store.execute(new GetElements.Builder()
                .input(new Edge.Builder()
                        .group(TEST_EDGE)
                        .source(new TypeSubTypeValue(A, AA, AAA))
                        .dest(new TypeSubTypeValue(B, BB, BBB))
                        .build())
                .inOutType(SeededGraphFilters.IncludeIncomingOutgoingType.EITHER)
                .build(), CONTEXT);

        Assert.assertNotNull(results);
        //Null pointer based on missing view group???
        Assert.assertTrue(results.iterator().hasNext());

        ArrayList<Object> list = new ArrayList<>();
        for (Element result : results) {
            list.add(result);
        }

        Assert.assertEquals(1, list.size());
        Assert.assertEquals(EXPECTED, new String(JSONSerialiser.serialise(list.get(0))));

    }  @Test
    public void testGetAllElementsFromAccumulo() throws Exception {
        AccumuloStore store = getAccumuloStore();

        addElement(store);

        CloseableIterable<? extends Element> results = store.execute(new GetAllElements.Builder()
                .view(new View.Builder().edge(TEST_EDGE).build())
                .build(), CONTEXT);

        Assert.assertNotNull(results);
        Assert.assertTrue(results.iterator().hasNext());

        ArrayList<Object> list = new ArrayList<>();
        for (Element result : results) {
            list.add(result);
        }

        Assert.assertEquals(1, list.size());
        //This Json element has additional value not found in same MapStore query.
        Assert.assertEquals(EXPECTED, new String(JSONSerialiser.serialise(list.get(0), true)));

    }

    @Test
    public void testGetElementsFromAccumulo() throws Exception {
        AccumuloStore store = getAccumuloStore();

        addElement(store);

        CloseableIterable<? extends Element> results = store.execute(new GetElements.Builder()
                .input(new Edge.Builder()
                        .group(TEST_EDGE)
                        .source(new TypeSubTypeValue(A, AA, AAA))
                        //        .source(A)
                        .dest(new TypeSubTypeValue(B, BB, BBB))
                        //        .dest(B)
                        .build())
                .view(new View.Builder().build())
                .inOutType(SeededGraphFilters.IncludeIncomingOutgoingType.EITHER)
                .build(), CONTEXT);

        Assert.assertNotNull(results);
        Assert.assertTrue(results.iterator().hasNext());

        ArrayList<Object> list = new ArrayList<>();
        for (Element result : results) {
            list.add(result);
        }

        Assert.assertEquals(1, list.size());
        Assert.assertEquals(EXPECTED, new String(JSONSerialiser.serialise(list.get(0))));
    }

    @Test
    public void testGetElementsNoViewFromAccumulo() throws Exception {
        AccumuloStore store = getAccumuloStore();

        addElement(store);

        CloseableIterable<? extends Element> results = store.execute(new GetElements.Builder()
                .input(new Edge.Builder()
                        .group(TEST_EDGE)
                        .source(new TypeSubTypeValue(A, AA, AAA))
                        .dest(new TypeSubTypeValue(B, BB, BBB))
                        .build())
                .inOutType(SeededGraphFilters.IncludeIncomingOutgoingType.EITHER)
                .build(), CONTEXT);

        Assert.assertNotNull(results);
        Assert.assertTrue(results.iterator().hasNext());

        ArrayList<Object> list = new ArrayList<>();
        for (Element result : results) {
            list.add(result);
        }

        Assert.assertEquals(1, list.size());
        Assert.assertEquals(EXPECTED, new String(JSONSerialiser.serialise(list.get(0))));

    }

    private MapStore getMapStore() throws StoreException {
        MapStore store = new MapStore();

        store.initialise("store1", new Schema.Builder()
                .edge(TEST_EDGE, new SchemaEdgeDefinition.Builder()
                        .source(TSTV)
                        .destination(TSTV)
                        .build())
                .type(TSTV, TypeSubTypeValue.class)
                .build(), new MapStoreProperties());
        return store;
    }

    private AccumuloStore getAccumuloStore() throws StoreException {
        AccumuloStore store = new SingleUseMockAccumuloStore();

        store.initialise("store1", new Schema.Builder()
                .edge(TEST_EDGE, new SchemaEdgeDefinition.Builder()
                        .source(TSTV)
                        .destination(TSTV)
                        .build())
                .type(TSTV, TypeSubTypeValue.class)
                .build(), new AccumuloProperties());
        return store;
    }

    private void addElement(final Store store) throws OperationException {
        store.execute(new AddElements.Builder()
                .input(new Edge.Builder()
                        .group(TEST_EDGE)
                        .source(new TypeSubTypeValue(A, AA, AAA))
                        .dest(new TypeSubTypeValue(B, BB, BBB))
                        .build())
                .build(), CONTEXT);
    }

}

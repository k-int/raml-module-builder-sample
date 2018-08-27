package org.folio.support.matchers;

import io.vertx.core.json.JsonObject;
import org.folio.support.JsonArrayHelper;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;
import org.hamcrest.core.IsCollectionContaining;

import java.util.List;
import java.util.Objects;

import static org.hamcrest.CoreMatchers.is;

public class ValidationErrorMatchers {
  public static TypeSafeDiagnosingMatcher<JsonObject> hasErrorWith(Matcher<JsonObject> matcher) {
    return new TypeSafeDiagnosingMatcher<JsonObject>() {
      @Override
      public void describeTo(Description description) {
        description
          .appendText("Validation error which ").appendDescriptionOf(matcher);
      }

      @Override
      protected boolean matchesSafely(JsonObject representation, Description description) {
        final Matcher<Iterable<? super JsonObject>> iterableMatcher = IsCollectionContaining.hasItem(matcher);
        final List<JsonObject> errors = JsonArrayHelper.toList(representation, "errors");

        iterableMatcher.describeMismatch(errors, description);

        return iterableMatcher.matches(errors);
      }
    };
  }

  public static TypeSafeDiagnosingMatcher<JsonObject> hasParameter(String key, String value) {
    return new TypeSafeDiagnosingMatcher<JsonObject>() {
      @Override
      public void describeTo(Description description) {
        description.appendText("has parameter with key ").appendValue(key)
          .appendText(" and value ").appendValue(value);
      }

      @Override
      protected boolean matchesSafely(JsonObject error, Description description) {
        final List<JsonObject> parameters = JsonArrayHelper.toList(error, "parameters");

        final boolean hasParameter = hasParameter(parameters, key, value);

        if(!hasParameter) {
          if(!hasParameter(parameters, key)) {
            description.appendText("does not have parameter ")
              .appendValue(key);
          }
          else {
            description.appendText("parameter has value ")
              .appendValue(getParameter(parameters, key));
          }
        }

        return hasParameter;
      }
    };
  }

  private static String getParameter(List<JsonObject> parameters, String key) {
    return parameters.stream().filter(parameter ->
      Objects.equals(parameter.getString("key"), key))
      .findFirst()
      .map(parameter -> parameter.getString("value"))
      .orElse(null);
  }

  private static boolean hasParameter(List<JsonObject> parameters, String key) {
    return parameters.stream().anyMatch(parameter ->
      Objects.equals(parameter.getString("key"), key));
  }

  private static boolean hasParameter(List<JsonObject> parameters, String key, String value) {
    return parameters.stream().anyMatch(parameter ->
      Objects.equals(parameter.getString("key"), key)
        && Objects.equals(parameter.getString("value"), value));
  }

  public static TypeSafeDiagnosingMatcher<JsonObject> hasMessage(String message) {
    return new TypeSafeDiagnosingMatcher<JsonObject>() {
      @Override
      public void describeTo(Description description) {
        description.appendText("has message ").appendValue(message);
      }

      @Override
      protected boolean matchesSafely(JsonObject error, Description description) {
        final Matcher<String> matcher = is(message);

        matcher.describeMismatch(error, description);

        return matcher.matches(error.getString("message"));
      }
    };
  }
}

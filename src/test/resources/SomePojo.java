import lombok.Builder;
import lombok.Value;

@Value
@Builder(toBuilder = true)
class SomePojo {

	/**
	 * Some string field
	 */
	String someStringField;
	/**
	 * Another string field
	 */
	String anotherStringField;
}

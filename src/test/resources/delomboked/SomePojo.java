
final class SomePojo {
	/**
	 * Some string field
	 */
	private final String someStringField;
	/**
	 * Another string field
	 */
	private final String anotherStringField;

	SomePojo(final String someStringField, final String anotherStringField) {
		this.someStringField = someStringField;
		this.anotherStringField = anotherStringField;
	}


	public static class SomePojoBuilder {
		private String someStringField;
		private String anotherStringField;

		SomePojoBuilder() {
		}

		public SomePojoBuilder someStringField(final String someStringField) {
			this.someStringField = someStringField;
			return this;
		}

		public SomePojoBuilder anotherStringField(final String anotherStringField) {
			this.anotherStringField = anotherStringField;
			return this;
		}

		public SomePojo build() {
			return new SomePojo(someStringField, anotherStringField);
		}

		@java.lang.Override
		public java.lang.String toString() {
			return "SomePojo.SomePojoBuilder(someStringField=" + this.someStringField + ", anotherStringField=" + this.anotherStringField + ")";
		}
	}

	public static SomePojoBuilder builder() {
		return new SomePojoBuilder();
	}

	public SomePojoBuilder toBuilder() {
		return new SomePojoBuilder().someStringField(this.someStringField).anotherStringField(this.anotherStringField);
	}

	/**
	 * Some string field
	 */
	public String getSomeStringField() {
		return this.someStringField;
	}

	/**
	 * Another string field
	 */
	public String getAnotherStringField() {
		return this.anotherStringField;
	}

	@java.lang.Override
	public boolean equals(final java.lang.Object o) {
		if (o == this) return true;
		if (!(o instanceof SomePojo)) return false;
		final SomePojo other = (SomePojo) o;
		final java.lang.Object this$someStringField = this.getSomeStringField();
		final java.lang.Object other$someStringField = other.getSomeStringField();
		if (this$someStringField == null ? other$someStringField != null : !this$someStringField.equals(other$someStringField)) return false;
		final java.lang.Object this$anotherStringField = this.getAnotherStringField();
		final java.lang.Object other$anotherStringField = other.getAnotherStringField();
		if (this$anotherStringField == null ? other$anotherStringField != null : !this$anotherStringField.equals(other$anotherStringField)) return false;
		return true;
	}

	@java.lang.Override
	public int hashCode() {
		final int PRIME = 59;
		int result = 1;
		final java.lang.Object $someStringField = this.getSomeStringField();
		result = result * PRIME + ($someStringField == null ? 43 : $someStringField.hashCode());
		final java.lang.Object $anotherStringField = this.getAnotherStringField();
		result = result * PRIME + ($anotherStringField == null ? 43 : $anotherStringField.hashCode());
		return result;
	}

	@java.lang.Override
	public java.lang.String toString() {
		return "SomePojo(someStringField=" + this.getSomeStringField() + ", anotherStringField=" + this.getAnotherStringField() + ")";
	}
}

package com.picsauditing.strutsutil.actionmapper;

final class RestMethodMapperConfig {

    private final String idParameterName;
    private final String indexMethodName;
    private final String getMethodName;
    private final String postMethodName;
    private final String editMethodName;
    private final String newMethodName;
    private final String deleteMethodName;
    private final String putMethodName;

    public RestMethodMapperConfig(Builder builder) {
        this.idParameterName = builder.idParameterName;
        this.indexMethodName = builder.indexMethodName;
        this.getMethodName = builder.getMethodName;
        this.postMethodName = builder.postMethodName;
        this.editMethodName = builder.editMethodName;
        this.newMethodName = builder.newMethodName;
        this.deleteMethodName = builder.deleteMethodName;
        this.putMethodName = builder.putMethodName;
    }

    public String getIdParameterName() {
        return idParameterName;
    }

    public String getIndexMethodName() {
        return indexMethodName;
    }

    public String getGetMethodName() {
        return getMethodName;
    }

    public String getPostMethodName() {
        return postMethodName;
    }

    public String getEditMethodName() {
        return editMethodName;
    }

    public String getNewMethodName() {
        return newMethodName;
    }

    public String getDeleteMethodName() {
        return deleteMethodName;
    }

    public String getPutMethodName() {
        return putMethodName;
    }

    public static class Builder {

        private String idParameterName = "id";
        private String indexMethodName = "index";
        private String getMethodName = "show";
        private String postMethodName = "create";
        private String editMethodName = "edit";
        private String newMethodName = "editNew";
        private String deleteMethodName = "destroy";
        private String putMethodName = "update";

        public Builder idParameterName(String idParameterName) {
            this.idParameterName = idParameterName;
            return this;
        }

        public Builder indexMethodName(String indexMethodName) {
            this.indexMethodName = indexMethodName;
            return this;
        }

        public Builder getMethodName(String getMethodName) {
            this.getMethodName = getMethodName;
            return this;
        }

        public Builder postMethodName(String postMethodName) {
            this.postMethodName = postMethodName;
            return this;
        }

        public Builder editMethodName(String editMethodName) {
            this.editMethodName = editMethodName;
            return this;
        }

        public Builder newMethodName(String newMethodName) {
            this.newMethodName = newMethodName;
            return this;
        }

        public Builder deleteMethodName(String deleteMethodName) {
            this.deleteMethodName = deleteMethodName;
            return this;
        }

        public Builder putMethodName(String putMethodName) {
            this.putMethodName = putMethodName;
            return this;
        }

        public RestMethodMapperConfig build() {
            return new RestMethodMapperConfig(this);
        }
    }
}

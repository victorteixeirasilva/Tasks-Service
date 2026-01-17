package tech.inovasoft.inevolving.ms.tasks.service.client.Auth_For_MService;

import lombok.Getter;

@Getter
public enum MicroServices {
    BOOKS_SERVICE("books-service"),
    GATEWAY_SERVICE("gateway-service"),
    CATEGORIES_SERVICE("categories-service"),
    DASHBOARD_SERVICE("dashboard-service"),
    EMAIL_SERVICE("email-service"),
    FINANCE_SERVICE("finance-service"),
    MOTIVATION_SERVICE("motivation-service"),
    OBJECTIVES_SERVICE("objectives-service"),
    TASKS_SERVICE("tasks-service");


    private final String value;

    MicroServices(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}

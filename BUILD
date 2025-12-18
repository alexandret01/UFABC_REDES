COMPILE_DEPS = CORE_DEPS + JACKSON + [
    "//models/padtec:onos-models-padtec",
    "//drivers/utilities:onos-drivers-utilities",
    "//protocols/gnmi/api:onos-protocols-gnmi-api",
    "//apps/optical-model:onos-apps-optical-model",
]

APPS = [
    "org.onosproject.models.common",
    "org.onosproject.models.padtec",
    "org.onosproject.optical-model",
    "org.onosproject.faultmanagement",
]

TEST_DEPS = TEST_ADAPTERS + [
    "//utils/osgi:onlab-osgi-tests",
]

BUNDLES = [
    ":onos-drivers-padtec",
    "//drivers/utilities:onos-drivers-utilities",
]

osgi_jar_with_tests(
    resources = glob(["src/main/resources/**"]),
    resources_root = "src/main/resources",
    test_deps = TEST_DEPS,
    deps = COMPILE_DEPS,
)

onos_app(
    app_name = "org.onosproject.drivers.padtec",
    category = "Drivers",
    description = "ONOS Padtec Device Drivers application.",
    included_bundles = BUNDLES,
    required_apps = APPS,
    title = "Padtec Device Drivers",
    url = "http://optinet.ufabc.edu.br",
)

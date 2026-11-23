import argparse
import unittest

from scripts import generar_dashboard


class DashboardTest(unittest.TestCase):
    def args(self, code=None, count=None):
        return argparse.Namespace(
            sha="<sha&>",
            run_url="https://example.test/run?a=<b>&x=1",
            environment="kind&test",
            http_rejection_code=code,
            http_rejection_count=count,
        )

    def render(self, metrics, code=401, count=5):
        return generar_dashboard.generar_documento(
            self.args(code, count), (8, 2, 80.0), 2.5, *metrics
        )

    def test_all_metrics_and_401(self):
        document = self.render((12.34, 256.0, 0.1256, 0.0), 401, 5)
        self.assertIn("80.00%", document)
        self.assertIn("2.5 s", document)
        self.assertIn("12.34 %", document)
        self.assertIn("256.0 MiB", document)
        self.assertIn("HTTP 401 de prueba", document)
        self.assertIn("5 respuestas", document)
        self.assertIn("0.1256 /s", document)
        self.assertIn("MS-AUTH Dashboard", document)

    def test_missing_metrics_and_403(self):
        document = self.render((None, None, None, None), 403, None)
        self.assertGreaterEqual(document.count("Sin datos"), 5)
        self.assertIn("HTTP 403 de prueba", document)
        self.assertNotIn("None", document)

    def test_dynamic_values_are_escaped(self):
        document = self.render((1.0, 2.0, 3.0, 4.0))
        self.assertIn("&lt;sha&amp;&gt;", document)
        self.assertIn("kind&amp;test", document)
        self.assertIn("https://example.test/run?a=&lt;b&gt;&amp;x=1", document)
        self.assertNotIn("<sha&>", document)


if __name__ == "__main__":
    unittest.main()

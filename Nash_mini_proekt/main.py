import unittest
from prost import prost


class ProstTestCase(unittest.TestCase):
    def test_prost(self):
        self.assertEqual(prost(7), 'Да')
        self.assertEqual(prost(123), 'Нет')
        self.assertEqual(prost(67), 'Да')
        self.assertEqual(prost(68), 'Нет')
        self.assertEqual(prost(90), 'Нет')


if __name__ == '__main__':
    unittest.main()

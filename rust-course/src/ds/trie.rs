use std::collections::HashMap;
use std::hash::Hash;

#[derive(Debug, Default)]
struct Node<Key: Default, Type: Default> {
    children: HashMap<Key, Node<Key, Type>>,
    value: Option<Type>,
}

#[derive(Debug, Default)]
pub struct Trie<Key, Type>
where
    Key: Default + Eq + Hash,
    Type: Default,
{
    root: Node<Key, Type>,
}

impl<Key, Type> Trie<Key, Type>
where
    Key: Default + Eq + Hash,
    Type: Default,
{
    pub fn new() -> Self {
        Self {
            root: Node:: default(),
        }
    }

    pub fn insert(&mut self, key: impl IntoIterator<Item = Key>, value: Type)
    where Key: Eq + Hash,
    {
        let mut node = &mut self.root;
        for c in key.into_iter() {
            node = node.children.entry(c).or_insert_with(Node::default)
        }
        node.value = Some(value);
    }

    pub fn get(&self, key: impl IntoIterator<Item = Key>) -> Option<&Type>
    where Key: Eq + Hash,
    {
        let mut node = &self.root;
        for c in key.into_iter() {
            if node.children.contains_key(&c) {
                node = node.children.get(&c).unwrap();
            } else {
                return None;
            }
        }
        node.value.as_ref()
    }
}

#[cfg(test)]
mod tests {
    use crate::ds::trie::Trie;

    #[test]
    fn test_insertion() {
        let mut trie: Trie<char, usize> = Trie::new();
        assert_eq!(trie.get("".chars()), None);

        trie.insert("foo".chars(), 1);
        trie.insert("foobar".chars(), 2);
        assert_eq!(trie.get("foo".chars()), Some(&1));
        assert_eq!(trie.get("foobar".chars()), Some(&2));
    }

    #[test]
    fn test_insert_int() {
        let mut trie: Trie<i32, usize> = Trie::new();
        trie.insert(vec![1, 2, 3], 1);
        trie.insert(vec![3, 4, 5], 2);
        assert_eq!(trie.get(vec![1, 2, 3]), Some(&1))
    }

}